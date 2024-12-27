package com.example.jimsjohns.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.jimsjohns.Bathroom;
import com.example.jimsjohns.MapActivity;
import com.example.jimsjohns.R;
import com.example.jimsjohns.databinding.FragmentHomeBinding;
import com.example.jimsjohns.ui.BathroomForm;
import com.example.jimsjohns.ui.BathroomInfoWindowAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    ActivityResultLauncher<String> requestPermissionLauncher;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    ArrayList<Bathroom> bathrooms;
    private boolean isCreating = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton createButton = (FloatingActionButton) root.findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCreating) {
                    isCreating = false;
                    v.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.button));
                } else {
                    isCreating = true;
                    Toast.makeText(getActivity(), "Tap to create a new Bathroom marker", Toast.LENGTH_SHORT).show();
                    v.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.selectedbutton));
                }
            }
        });

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean o) {
                        if(o) {
                            getLastLocation();
                        } else {
                            Toast.makeText(getActivity(), "Location Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        getLastLocation();

        return root;
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @SuppressLint({"MissingPermission", "PotentialBehaviorOverride"})
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                // When map is loaded
                                myMap = googleMap;
                                myMap.setInfoWindowAdapter(new BathroomInfoWindowAdapter(getActivity()));

                                LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                myMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                                myMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                                myMap.setMyLocationEnabled(true);

                                loadMarkers();

                                myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(@NonNull LatLng latLng) {
                                        if(isCreating) {
                                            myMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                            getChildFragmentManager().setFragmentResultListener(
                                                    "bathroom",
                                                    HomeFragment.this, new FragmentResultListener() {
                                                @Override
                                                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                                                    Bathroom bathroom = BundleCompat.getSerializable(result, "bathroom", Bathroom.class);
                                                    myMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(
                                                                    bathroom.getLatitude(),
                                                                    bathroom.getLongitude()))
                                                            .title(bathroom.getName())
                                                            .snippet(bathroom.getSnippet())
                                                    );
                                                    DatabaseReference dbReference = firebaseDatabase.getReference("Bathrooms");
                                                    String key = dbReference.push().getKey();
                                                    dbReference.child(key).setValue(bathroom);
                                                }
                                            });
                                            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                            DialogFragment bathroomForm = BathroomForm.newInstance(
                                                    latLng.latitude, latLng.longitude, null);
                                            bathroomForm.show(ft, "bathroomForm");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(myMap != null) {
            myMap.clear();
            loadMarkers();
        }
    }

    public void loadMarkers() {
        DatabaseReference dbReference = firebaseDatabase.getReference("Bathrooms");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Bathroom bathroom = dataSnapshot.getValue(Bathroom.class);
                    myMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    bathroom.getLatitude(),
                                    bathroom.getLongitude()))
                            .title(bathroom.getName())
                            .snippet(bathroom.getSnippet()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
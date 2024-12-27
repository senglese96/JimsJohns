package com.example.jimsjohns.ui.my_bathrooms;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jimsjohns.Bathroom;
import com.example.jimsjohns.BathroomAdapter;
import com.example.jimsjohns.BathroomFormListener;
import com.example.jimsjohns.databinding.FragmentMyBathroomsBinding;
import com.example.jimsjohns.ui.BathroomForm;
import com.example.jimsjohns.ui.home.HomeFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

public class MyBathroomsFragment extends Fragment implements BathroomFormListener {
    private DatabaseReference databaseReference;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<Bathroom> bathrooms;
    private RecyclerView recyclerView;
    private BathroomAdapter bathroomAdapter;
    private FragmentMyBathroomsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyBathroomsViewModel galleryViewModel =
                new ViewModelProvider(this).get(MyBathroomsViewModel.class);

        binding = FragmentMyBathroomsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SearchView bathroomSearch = binding.bathroomSearch;
        bathroomSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                databaseReference = database.getReference("Bathrooms");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bathrooms.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Bathroom bathroom = dataSnapshot.getValue(Bathroom.class);
                            if (Objects.equals(bathroom.getCreatedBy(), mAuth.getCurrentUser().getUid())
                                    && bathroom.getName().contains(newText)) {
                                bathroom.setId(dataSnapshot.getKey());
                                bathrooms.add(bathroom);
                            }
                        }

                        bathroomAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });

        databaseReference = database.getReference("Bathrooms");
        recyclerView = binding.recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bathrooms = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bathrooms.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Bathroom bathroom = dataSnapshot.getValue(Bathroom.class);
                    if(Objects.equals(bathroom.getCreatedBy(), mAuth.getCurrentUser().getUid())) {
                        bathroom.setId(dataSnapshot.getKey());
                        bathrooms.add(bathroom);
                    }
                }

                bathroomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bathroomAdapter = new BathroomAdapter(this, bathrooms);
        recyclerView.setAdapter(bathroomAdapter);
        binding.setBathroomAdapter(bathroomAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditClick(Bathroom currentBathroom) {
        getChildFragmentManager().setFragmentResultListener(
                "bathroom",
                MyBathroomsFragment.this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        Bathroom bathroom = BundleCompat.getSerializable(result, "bathroom", Bathroom.class);
                        DatabaseReference dbReference = database.getReference("Bathrooms");
                        dbReference.child(bathroom.getId()).setValue(bathroom);
                    }
                });
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        DialogFragment bathroomForm = BathroomForm.newInstance(
                currentBathroom.getLatitude(), currentBathroom.getLongitude(), currentBathroom);
        bathroomForm.show(ft, "bathroomForm");
    }


}
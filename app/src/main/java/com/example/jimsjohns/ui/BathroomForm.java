package com.example.jimsjohns.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.core.os.BundleCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jimsjohns.Bathroom;
import com.example.jimsjohns.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BathroomForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BathroomForm extends DialogFragment {

    private static final String ARG_LAT = "latitude";
    private static final String ARG_LNG = "longitude";
    private static final String ARG_BTH = "bathroom";

    private double latitude;
    private double longitude;
    Spinner ratingSpinner;
    Button submitButton;
    EditText nameText;
    EditText descriptionText;
    CheckBox neutralCheckBox;
    CheckBox purchaseCheckBox;
    Bathroom bathroom;
    String id;

    public BathroomForm() {
        // Required empty public constructor
    }

    public static BathroomForm newInstance(double latitude, double longitude, Bathroom bathroom) {
        BathroomForm fragment = new BathroomForm();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, latitude);
        args.putDouble(ARG_LNG, longitude);
        args.putSerializable(ARG_BTH, bathroom);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LAT);
            longitude = getArguments().getDouble(ARG_LNG);
            bathroom = BundleCompat.getSerializable(getArguments(), "bathroom", Bathroom.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bathroom_form, container, false);

        nameText = view.findViewById(R.id.nameText);
        descriptionText = view.findViewById(R.id.descriptionText);
        neutralCheckBox = view.findViewById(R.id.neutralCheckBox);
        purchaseCheckBox = view.findViewById(R.id.purchaseCheckBox);
        ratingSpinner= view.findViewById(R.id.ratingSpinner);
        Integer[] items = new Integer[]{1,2,3,4,5};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
        ratingSpinner.setAdapter(adapter);
        submitButton = (Button) view.findViewById(R.id.submitButton);

        if(bathroom != null) {
            nameText.setText(bathroom.getName());
            descriptionText.setText(bathroom.getDescription());
            if(bathroom.isGenderNeutral()) { neutralCheckBox.setChecked(true); }
            if(bathroom.isPurchaseNecessary()) {purchaseCheckBox.setChecked(true);}
            ratingSpinner.setSelection(bathroom.getRating() - 1);
            id = bathroom.getId();
            submitButton.setText("Edit");
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(nameText.getText().toString(), descriptionText.getText().toString())) {
                    Bathroom bathroom = new Bathroom();
                    bathroom.setName(nameText.getText().toString());
                    bathroom.setDescription(descriptionText.getText().toString());
                    bathroom.setGenderNeutral(neutralCheckBox.isChecked());
                    bathroom.setPurchaseNecessary(purchaseCheckBox.isChecked());
                    bathroom.setRating((Integer) ratingSpinner.getSelectedItem());
                    bathroom.setLatitude(latitude);
                    bathroom.setLongitude(longitude);
                    bathroom.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    bathroom.setId(id);

                    Bundle result = new Bundle();
                    result.putSerializable("bathroom", bathroom);

                    getParentFragmentManager().setFragmentResult("bathroom", result);
                    dismiss();
                } else {
                    Toast.makeText(requireContext(), "Invalid input (Input is either too long, or too short)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public boolean validate(String name, String description) {
        if(name.isEmpty()) {
            return false;
        } if(name.length() > 50 || description.length() > 150){
            return false;
        }else {
            return true;
        }
    }
}
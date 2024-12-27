package com.example.jimsjohns;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.os.BundleCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jimsjohns.databinding.BathroomCardViewBinding;
import com.example.jimsjohns.ui.BathroomForm;
import com.example.jimsjohns.ui.home.HomeFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BathroomAdapter extends RecyclerView.Adapter<BathroomAdapter.BathroomViewHolder>{
    private BathroomFormListener bathroomFormListener;
    private ArrayList<Bathroom> bathroomList;

    public BathroomAdapter(BathroomFormListener bathroomFormListener, ArrayList<Bathroom> bathroomList) {
        this.bathroomFormListener = bathroomFormListener;
        this.bathroomList = bathroomList;
    }

    @NonNull
    @Override
    public BathroomAdapter.BathroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BathroomCardViewBinding binding = DataBindingUtil
                .inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.bathroom_card_view,
                        parent,
                        false
                );
        return new BathroomAdapter.BathroomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BathroomAdapter.BathroomViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Bathroom currentBathroom = bathroomList.get(position);
        holder.bathroomCardViewBinding.setBathroom(currentBathroom);
        holder.bathroomCardViewBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbReference = database.getReference("Bathrooms");
                dbReference.child(currentBathroom.getId()).removeValue();
            }
        });
        holder.bathroomCardViewBinding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bathroomFormListener.onEditClick(currentBathroom);
            }
        });
        TextView genderNeutral = holder.bathroomCardViewBinding.genderDisplay;
        TextView purchaseNecessary = holder.bathroomCardViewBinding.purchaseDisplay;
        if(currentBathroom.isGenderNeutral()) {
            genderNeutral.setText("Gender Neutral: Y");
        } else {
            genderNeutral.setText("Gender Neutral: N");
        }
        if(currentBathroom.isPurchaseNecessary()){
            purchaseNecessary.setText("Purchase Required: Y");
        } else {
            purchaseNecessary.setText("Purchase Required: N");
        }
    }

    @Override
    public int getItemCount() {
        return bathroomList.size();
    }

    public class BathroomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private BathroomCardViewBinding bathroomCardViewBinding;
        public BathroomViewHolder(BathroomCardViewBinding bathroomCardViewBinding) {
            super(bathroomCardViewBinding.getRoot());
            this.bathroomCardViewBinding = bathroomCardViewBinding;
        }

        @Override
        public void onClick(View v) {
        }
    }
}


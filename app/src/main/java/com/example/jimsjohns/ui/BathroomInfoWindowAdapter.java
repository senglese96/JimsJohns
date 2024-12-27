package com.example.jimsjohns.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.jimsjohns.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class BathroomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    View mWindow;
    public BathroomInfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.bathroom_info_window, null);
    }

    private void setInfoWindowText(Marker marker) {
        String title = marker.getTitle();
        TextView infoTitle = mWindow.findViewById(R.id.infoTitle);
        String snippet = marker.getSnippet();

        TextView infoSnippet = mWindow.findViewById(R.id.infoSnippet);
        infoTitle.setText(title);
        infoSnippet.setText(snippet);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        setInfoWindowText(marker);
        return mWindow;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        setInfoWindowText(marker);
        return mWindow;
    }
}

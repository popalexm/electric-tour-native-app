package com.grandtour.ev.evgrandtour.ui.mainMapsView.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import android.support.annotation.NonNull;

public class MapCheckpoint implements ClusterItem {

    @NonNull
    private final LatLng position;

    public MapCheckpoint(@NonNull LatLng position) {
        this.position = position;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }
}

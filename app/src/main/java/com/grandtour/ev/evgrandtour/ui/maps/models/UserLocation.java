package com.grandtour.ev.evgrandtour.ui.maps.models;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import android.support.annotation.NonNull;

public class UserLocation {

    @NonNull
    private Marker currentLocationMarker;
    @NonNull
    private Circle currentLocationCircle;

    public UserLocation(@NonNull Marker currentLocationMarker, @NonNull Circle currentLocationCircle) {
        this.currentLocationMarker = currentLocationMarker;
        this.currentLocationCircle = currentLocationCircle;
    }

    @NonNull
    public Marker getCurrentLocationMarker() {
        return currentLocationMarker;
    }

    @NonNull
    public Circle getCurrentLocationCircle() {
        return currentLocationCircle;
    }
}

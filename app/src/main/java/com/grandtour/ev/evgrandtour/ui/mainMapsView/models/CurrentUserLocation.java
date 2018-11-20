package com.grandtour.ev.evgrandtour.ui.mainMapsView.models;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import android.support.annotation.NonNull;

public class CurrentUserLocation {

    @NonNull
    private final Marker currentLocationMarker;
    @NonNull
    private final Circle currentLocationCircle;

    public CurrentUserLocation(@NonNull Marker currentLocationMarker, @NonNull Circle currentLocationCircle) {
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

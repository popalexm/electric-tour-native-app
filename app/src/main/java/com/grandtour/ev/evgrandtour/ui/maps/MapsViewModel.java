package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import com.grandtour.ev.evgrandtour.ui.maps.models.UserLocation;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class MapsViewModel {

    @NonNull
    public final ObservableField<String> progressMessage = new ObservableField<>("");
    @NonNull
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isCancelEnabled = new ObservableBoolean();
    @NonNull
    public final ObservableArrayList<Marker> checkpoints = new ObservableArrayList<>();
    @NonNull
    public final ObservableArrayList<Polyline> routes = new ObservableArrayList<>();
    @NonNull
    public final ObservableField<UserLocation> currentUserLocation = new ObservableField<>();
    @NonNull
    public final ObservableField<Marker> currentSelectedMarker = new ObservableField<>();
    @NonNull
    public final ObservableField<String> totalDistance = new ObservableField<>("");
    @NonNull
    public final ObservableBoolean isRouteLengthAvailable = new ObservableBoolean();

}

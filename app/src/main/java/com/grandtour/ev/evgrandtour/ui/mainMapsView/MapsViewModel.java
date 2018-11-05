package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.CurrentUserLocation;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class MapsViewModel {

    @NonNull
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    @NonNull
    public final ObservableArrayList<Marker> checkpoints = new ObservableArrayList<>();
    @NonNull
    public final ObservableArrayList<Polyline> routes = new ObservableArrayList<>();
    @NonNull
    public final ObservableField<CurrentUserLocation> currentUserLocation = new ObservableField<>();
    @NonNull
    public final ObservableField<Marker> currentSelectedMarker = new ObservableField<>();
    @NonNull
    public final ObservableField<String> totalRouteInformation = new ObservableField<>("");
    @NonNull
    public final ObservableBoolean isRouteLengthAvailable = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isSearchViewOpen = new ObservableBoolean();

}

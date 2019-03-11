package com.grandtour.ev.evgrandtour.ui.currentTripView.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import androidx.annotation.NonNull;

public class MapCheckpoint implements ClusterItem {

    @NonNull
    private final LatLng position;
    @NonNull
    private final Integer mapCheckpointId;
    @NonNull
    private final Integer orderInRouteId;
    @NonNull
    private final String mapCheckpointTitle;
    @NonNull
    private final String mapCheckpointDetails;
    private final int markerIconColor;

    public MapCheckpoint(@NonNull LatLng position, @NonNull Integer mapCheckpointId, @NonNull Integer orderInRouteId, @NonNull String mapCheckpointTitle,
            @NonNull String mapCheckpointDetails, int markerIconColor) {
        this.position = position;
        this.mapCheckpointId = mapCheckpointId;
        this.orderInRouteId = orderInRouteId;
        this.mapCheckpointTitle = mapCheckpointTitle;
        this.mapCheckpointDetails = mapCheckpointDetails;
        this.markerIconColor = markerIconColor;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return mapCheckpointTitle;
    }

    @Override
    public String getSnippet() {
        return mapCheckpointDetails;
    }

    @NonNull
    public Integer getMapCheckpointId() {
        return mapCheckpointId;
    }

    @NonNull
    public String getMapCheckpointTitle() {
        return mapCheckpointTitle;
    }

    @NonNull
    public String getMapCheckpointDetails() {
        return mapCheckpointDetails;
    }

    @NonNull
    public int getMarkerIconColor() {
        return markerIconColor;
    }

    @NonNull
    public Integer getOrderInRouteId() {
        return orderInRouteId;
    }
}

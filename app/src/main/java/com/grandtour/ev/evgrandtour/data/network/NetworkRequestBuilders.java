package com.grandtour.ev.evgrandtour.data.network;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteDirectionsRequest;

import android.support.annotation.NonNull;

import java.util.List;

public final class NetworkRequestBuilders {

    @NonNull
    private static final String DIRECTIONS_REQUEST_MODE = "driving";

    private NetworkRequestBuilders() {
    }

    @NonNull
    public static RouteDirectionsRequest createDirectionRequestParams(@NonNull List<LatLng> checkpoints, @NonNull String apiKey) {
        LatLng startCheckpoint = checkpoints.get(0);
        LatLng endCheckpoint = checkpoints.get(checkpoints.size() - 1);
        checkpoints.remove(startCheckpoint);
        checkpoints.remove(endCheckpoint);
        return new RouteDirectionsRequest.RouteParametersBuilder().setStartWaypoint(startCheckpoint)
                .setEndWaypoint(endCheckpoint)
                .setTransitWaypoints(checkpoints)
                .setMode(NetworkRequestBuilders.DIRECTIONS_REQUEST_MODE)
                .setAPIKey(apiKey)
                .createRouteParameters();
    }

    @NonNull
    public static String createElevationRequest(@NonNull List<ElevationPoint> elevationPoints) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elevationPoints.size(); i++) {
            ElevationPoint elevationPoint = elevationPoints.get(i);
            builder.append(elevationPoint.getLatitude());
            builder.append(",");
            builder.append(elevationPoint.getLongitude());
            if (i < elevationPoints.size() - 1) {
                builder.append("|");
            }
        }
        return builder.toString();
    }
}

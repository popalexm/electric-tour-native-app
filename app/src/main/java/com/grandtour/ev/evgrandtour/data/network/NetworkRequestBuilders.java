package com.grandtour.ev.evgrandtour.data.network;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedCheckpointRequest;
import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedTripRequest;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteDirectionsRequest;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class NetworkRequestBuilders {

    @NonNull
    private static final String DIRECTIONS_REQUEST_MODE = "driving";

    private NetworkRequestBuilders() {
    }

    @NonNull
    public static RouteDirectionsRequest generateDirectionRequestParameters(@NonNull List<Checkpoint> checkpoints, @NonNull String apiKey) {
        List<LatLng> checkpointLatLng = new ArrayList<>();
        for (int index = 0; index < checkpoints.size(); index++) {
            Checkpoint checkpoint = checkpoints.get(index);
            checkpointLatLng.add(new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude()));
        }

        LatLng startCheckpoint = checkpointLatLng.get(0);
        LatLng endCheckpoint = checkpointLatLng.get(checkpointLatLng.size() - 1);
        checkpointLatLng.remove(startCheckpoint);
        checkpointLatLng.remove(endCheckpoint);
        return new RouteDirectionsRequest.RouteParametersBuilder().setStartWaypoint(startCheckpoint)
                .setEndWaypoint(endCheckpoint)
                .setTransitWaypoints(checkpointLatLng)
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

    @NonNull
    public static PlannedTripRequest createPlannedTripRequest(@NonNull InPlanningTripDetails inPlanningTripDetails) {
        PlannedTripRequest plannedTripRequest = new PlannedTripRequest();

        int tripId = inPlanningTripDetails.getInPlanningTripId();
        plannedTripRequest.setTripId(tripId);
        plannedTripRequest.setTripName(inPlanningTripDetails.getInPlanningTripName());
        plannedTripRequest.setTripDescription(inPlanningTripDetails.getInPlanningTripDescription());

        List<TripCheckpoint> plannedTripCheckpoints = inPlanningTripDetails.getPlannedTripCheckpoints();
        if (plannedTripCheckpoints.size() > 0) {
            for (TripCheckpoint plannedCheckpoint : plannedTripCheckpoints) {
                PlannedCheckpointRequest checkpointRequest = new PlannedCheckpointRequest();
                checkpointRequest.setCheckpointId(plannedCheckpoint.getCheckpointId());
                checkpointRequest.setTripId(tripId);
                checkpointRequest.setCheckpointDescription(plannedCheckpoint.getCheckpointDescription());
                checkpointRequest.setCheckpointAddress(plannedCheckpoint.getCheckpointAddress());
                checkpointRequest.setCheckpointLatitude(plannedCheckpoint.getGeographicalPosition().latitude);
                checkpointRequest.setCheckpointLongitude(plannedCheckpoint.getGeographicalPosition().longitude);
                checkpointRequest.setAreArrivalNotificationsEnabled(plannedCheckpoint.isAreArrivalNotificationsEnabled());
                checkpointRequest.setAreDepartureNotificationsEnabled(plannedCheckpoint.isAreDepartureNotificationsEnabled());
                checkpointRequest.setCheckpointColor(plannedCheckpoint.getCheckpointColor());
            }
        }
        return plannedTripRequest;
    }
}

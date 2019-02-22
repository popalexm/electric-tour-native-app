package com.grandtour.ev.evgrandtour.domain.useCases.modelConversion;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.database.models.InPlanningCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

public final class ModelConversionUtils {

    private ModelConversionUtils() {
    }

    @NonNull
    public static InPlanningCheckpoint convertMapCheckpointModelToDatabaseModel(@NonNull TripCheckpoint tripCheckpoint, int tripId) {
        InPlanningCheckpoint inPlanningCheckpoint = new InPlanningCheckpoint();
        inPlanningCheckpoint.setTripId(tripId);
        inPlanningCheckpoint.setCheckpointTitle(tripCheckpoint.getCheckpointTitle());
        inPlanningCheckpoint.setCheckpointDescription(tripCheckpoint.getCheckpointDescription());
        inPlanningCheckpoint.setCheckpointAddress(tripCheckpoint.getCheckpointAddress());
        inPlanningCheckpoint.setCheckpointLatitude(tripCheckpoint.getGeographicalPosition().latitude);
        inPlanningCheckpoint.setCheckpointLongitude(tripCheckpoint.getGeographicalPosition().longitude);
        inPlanningCheckpoint.setAreArrivalNotificationsEnabled(tripCheckpoint.isAreArrivalNotificationsEnabled());
        inPlanningCheckpoint.setAreDepartureNotificationsEnabled(tripCheckpoint.isAreDepartureNotificationsEnabled());
        return inPlanningCheckpoint;
    }

    @NonNull
    public static TripCheckpoint convertDatabaseModelMapCheckpointModel(@NonNull InPlanningCheckpoint checkpoint) {
        TripCheckpoint tripCheckpoint = new TripCheckpoint();
        tripCheckpoint.setCheckpointId(checkpoint.getCheckpointId());
        tripCheckpoint.setCheckpointTitle(checkpoint.getCheckpointTitle());
        tripCheckpoint.setCheckpointDescription(checkpoint.getCheckpointDescription());
        tripCheckpoint.setCheckpointAddress(checkpoint.getCheckpointAddress());
        tripCheckpoint.setGeographicalPosition(new LatLng(checkpoint.getCheckpointLatitude(), checkpoint.getCheckpointLongitude()));
        tripCheckpoint.setAreArrivalNotificationsEnabled(checkpoint.isAreArrivalNotificationsEnabled());
        tripCheckpoint.setAreDepartureNotificationsEnabled(checkpoint.isAreDepartureNotificationsEnabled());
        return tripCheckpoint;
    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

public interface AddNewCheckpointDetailsCallback {

    void onCheckpointDetailsAdded(@NonNull TripCheckpoint tripCheckpoint);

}

package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.callbacks;

import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

public interface CheckpointDetailsCallback {

    void onCheckpointDetailsUpdated(@NonNull TripCheckpoint tripCheckpoint);

    void onCheckpointDeleted(int checkpointId);

}

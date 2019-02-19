package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.callbacks.CheckpointDetailsCallback;

import android.support.annotation.NonNull;

public class PlanNewTripContract {

    public interface View extends BaseContract.View, CheckpointDetailsCallback, ClusterManager.OnClusterClickListener<TripCheckpoint>,
            ClusterManager.OnClusterItemClickListener<TripCheckpoint> {

        void displayTripCheckpointOnMap(@NonNull TripCheckpoint newCheckpoint);

        void openNewCheckpointDetailsDialog(@NonNull LatLng clickedLocation);

        void displayInvalidTripNameWarning();
    }

    public interface Presenter extends BaseContract.Presenter {
        
        void onMapLocationClicked(@NonNull LatLng clickedLocation);

        void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint);

        void onDeleteCheckpointFromTrip(int checkpointId);

        void onMyLocationButtonClicked();

        void onFinalizeTripPlanningClicked(@NonNull String tripTitle);
    }
}

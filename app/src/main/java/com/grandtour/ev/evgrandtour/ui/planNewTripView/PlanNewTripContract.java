package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.callbacks.CheckpointDetailsCallback;

import android.support.annotation.NonNull;

import java.util.List;

public class PlanNewTripContract {

    public interface View extends BaseContract.View, CheckpointDetailsCallback, ClusterManager.OnClusterClickListener<TripCheckpoint>,
            ClusterManager.OnClusterItemClickListener<TripCheckpoint> {

        void displayNewTripCheckpointOnMap(@NonNull TripCheckpoint newCheckpoint);

        void displayPreviousTripCheckpointList(@NonNull List<TripCheckpoint> savedCheckpoints);

        void displayPreviousTripNameAndDescription(@NonNull String tripName, @NonNull String tripDescription);

        void openNewCheckpointDetailsDialog(@NonNull LatLng clickedLocation);

        void displayInvalidTripNameWarning();

        void removeAddedTripCheckpoint(@NonNull TripCheckpoint tripCheckpoint);
    }

    public interface Presenter extends BaseContract.Presenter {

        void onMapReady();
        
        void onMapLocationClicked(@NonNull LatLng clickedLocation);

        void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint);

        void onDeleteCheckpointFromTrip(@NonNull TripCheckpoint tripCheckpoint);

        void onMyLocationButtonClicked();

        void onFinalizeTripPlanningClicked(@NonNull String tripTitle);
    }
}

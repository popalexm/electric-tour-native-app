package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.AddNewCheckpointDetailsCallback;

import android.support.annotation.NonNull;

public class PlanNewTripContract {

    public interface View extends BaseContract.View, AddNewCheckpointDetailsCallback {

        void displayTripCheckpointOnMap(@NonNull TripCheckpoint newCheckpoint);

        void openNewCheckpointDetailsDialog(@NonNull LatLng clickedLocation);
    }

    public interface Presenter extends BaseContract.Presenter {

        void onAddNewCheckpointClicked();

        void onMapLocationClicked(@NonNull LatLng clickedLocation);

        void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint);
    }
}

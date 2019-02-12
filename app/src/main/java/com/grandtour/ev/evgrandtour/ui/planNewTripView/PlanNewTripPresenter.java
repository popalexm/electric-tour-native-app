package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter {

    @NonNull
    private final PlanNewTripContract.View view;
    private boolean isAddNewCheckpointFunctionEnabled;

    @NonNull
    private final List<TripCheckpoint> plannedTripCheckpoints = new ArrayList<>();

    PlanNewTripPresenter(@NonNull PlanNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onAddNewCheckpointClicked() {
        if (isViewAttached) {
            isAddNewCheckpointFunctionEnabled = true;
        }
    }

    @Override
    public void onMapLocationClicked(@NonNull LatLng clickedLocation) {
        if (isAddNewCheckpointFunctionEnabled && isViewAttached) {
            view.openNewCheckpointDetailsDialog(clickedLocation);
        }
    }

    @Override
    public void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint) {
        plannedTripCheckpoints.add(tripCheckpoint);
        if (isViewAttached) {
            view.displayTripCheckpointOnMap(tripCheckpoint);
        }
    }
}

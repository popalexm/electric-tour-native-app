package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter {

    @NonNull
    private final PlanNewTripContract.View view;
    private boolean isAddNewCheckpointFunctionEnabled;

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
            view.openNewCheckpointDetailsDialog();
        }
    }
}

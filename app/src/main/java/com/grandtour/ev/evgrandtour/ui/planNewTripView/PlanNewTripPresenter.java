package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter {

    @NonNull
    private PlanNewTripContract.View view;
    private boolean isAddNewCheckpointFuncitonEnabled;

    PlanNewTripPresenter(@NonNull PlanNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onAddNewCheckpointClicked() {
        if (isViewAttached) {
            isAddNewCheckpointFuncitonEnabled = true;
        }
    }
}

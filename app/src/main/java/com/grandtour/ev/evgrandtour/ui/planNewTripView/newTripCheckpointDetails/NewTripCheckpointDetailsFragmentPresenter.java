package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

public class NewTripCheckpointDetailsFragmentPresenter extends BasePresenter implements NewTripCheckpointDetailsFragmentContract.Presenter {

    @NonNull
    private final NewTripCheckpointDetailsFragmentContract.View view;

    public NewTripCheckpointDetailsFragmentPresenter(@NonNull NewTripCheckpointDetailsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onSaveCheckpointDetailsClicked() {

    }

    @Override
    public void onDismissButtonClicked() {
        if (isViewAttached) {
            view.dismissDetailsDialog();
        }
    }
}

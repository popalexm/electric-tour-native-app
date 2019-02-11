package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

public class NewTripCheckpointDetailsFragmentPresenter extends BasePresenter {

    @NonNull
    private final NewTripCheckpointDetailsFragmentContract.NewTripCheckpointDetailsFragmentView view;

    public NewTripCheckpointDetailsFragmentPresenter(@NonNull NewTripCheckpointDetailsFragmentContract.NewTripCheckpointDetailsFragmentView view) {
        this.view = view;
    }
}

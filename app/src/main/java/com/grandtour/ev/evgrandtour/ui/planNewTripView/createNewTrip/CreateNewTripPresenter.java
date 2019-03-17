package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import androidx.annotation.NonNull;

public class CreateNewTripPresenter extends BasePresenter implements CreateNewTripContract.Presenter {

    @NonNull
    private final CreateNewTripContract.View view;

    CreateNewTripPresenter(@NonNull CreateNewTripContract.View view) {
        this.view = view;
    }
}

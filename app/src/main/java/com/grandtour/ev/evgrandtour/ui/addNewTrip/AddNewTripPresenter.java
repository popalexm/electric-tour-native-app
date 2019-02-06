package com.grandtour.ev.evgrandtour.ui.addNewTrip;

import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

class AddNewTripPresenter extends BasePresenter implements AddNewTripContract.Presenter {

    @NonNull
    private AddNewTripContract.View view;

    AddNewTripPresenter(@NonNull AddNewTripContract.View view) {
        this.view = view;
    }
}

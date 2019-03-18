package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedTripRequest;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateNewTripPresenter extends BasePresenter implements CreateNewTripContract.Presenter {

    @NonNull
    private final CreateNewTripContract.View view;

    CreateNewTripPresenter(@NonNull CreateNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onNextPressed(@NonNull String newTripName, @NonNull String newTripDescription) {
        if (TextUtils.isEmpty(newTripName)) {
            if (isViewAttached()) {
                view.displayErrorOnTripNameField(Injection.provideGlobalContext()
                        .getString(R.string.error_trip_name_is_mandatory));
            }
        } else {
            if (isViewAttached) {
                view.removeErrorOnTripNameField();
            }
            PlannedTripRequest request = new PlannedTripRequest();
            request.setUserId(1);
            request.setTripName(newTripName);
            request.setTripDescription(newTripDescription);
            startAddTripRequest(request);
        }
    }

    private void startAddTripRequest(@NonNull PlannedTripRequest request) {
        addSubscription(Injection.provideCloudApi()
                .postPlannedTrip(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() == 200 && response.body() != null) {
                        onTripPlannedSuccessfully(response.body());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    onTripRequestFailed();
                }));
    }

    private void onTripPlannedSuccessfully(@NonNull Integer tripId) {
        if (isViewAttached()) {
            view.moveToTripCheckpointsPlanningScreen(tripId);
        }
    }

    private void onTripRequestFailed() {
        if (isViewAttached()) {
            view.showMessage("Error while trying to create your trip!");
        }
    }
}

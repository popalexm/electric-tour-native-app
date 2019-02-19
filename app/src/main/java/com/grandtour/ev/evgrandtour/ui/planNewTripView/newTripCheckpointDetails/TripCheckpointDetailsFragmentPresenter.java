package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.callbacks.AddNewCheckpointDetailsCallback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CompoundButton;

public class TripCheckpointDetailsFragmentPresenter extends BasePresenter implements TripCheckpointDetailsFragmentContract.Presenter {

    @NonNull
    private final TripCheckpointDetailsFragmentContract.View view;
    private boolean areArrivalNotificationsEnabled;
    private boolean areDepartureNotificationsEnabled;
    @Nullable
    private AddNewCheckpointDetailsCallback checkpointDetailsCallback;
    @Nullable
    private TripCheckpoint tripCheckpoint;

    TripCheckpointDetailsFragmentPresenter(@NonNull TripCheckpointDetailsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onInitCallbackToParentFragment(@NonNull AddNewCheckpointDetailsCallback checkpointDetailsCallback) {
        this.checkpointDetailsCallback = checkpointDetailsCallback;
    }

    @Override
    public void onRetrievedTripCheckpointDetailsFromBundle(@NonNull TripCheckpoint tripCheckpoint) {
        this.tripCheckpoint = tripCheckpoint;
        loadCheckpointDetails(tripCheckpoint);
    }

    @Override
    public void onSaveCheckpointDetailsClicked(@NonNull String checkpointName, @NonNull String checkpointDescription) {
        if (isViewAttached && TextUtils.isEmpty(checkpointName)) {
            view.shakeCheckpointNameTextView();
        } else if (tripCheckpoint != null && isViewAttached) {
            updateCheckpointDetails(checkpointName, checkpointDescription);
            if (checkpointDetailsCallback != null) {
                checkpointDetailsCallback.onCheckpointDetailsAdded(tripCheckpoint);
            }
            view.dismissDetailsDialog();
        }
    }

    private void updateCheckpointDetails(@NonNull String checkpointName, @NonNull String checkpointDescription) {
        if (tripCheckpoint != null) {
            tripCheckpoint.setCheckpointTitle(checkpointName);
            tripCheckpoint.setCheckpointDescription(checkpointDescription);
            tripCheckpoint.setAreArrivalNotificationsEnabled(areArrivalNotificationsEnabled);
            tripCheckpoint.setAreDepartureNotificationsEnabled(areDepartureNotificationsEnabled);
        }
    }

    @Override
    public void onNewCheckpointDetailsInitialised(@NonNull LatLng newCheckpointLocation) {
        tripCheckpoint = new TripCheckpoint();
        tripCheckpoint.setGeographicalPosition(newCheckpointLocation);
        searchForCheckpointAddress(newCheckpointLocation);
    }

    @Override
    public void onDismissButtonClicked() {
        if (isViewAttached) {
            view.dismissDetailsDialog();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchArrivalNotifications:
                areArrivalNotificationsEnabled = isChecked;
                break;
            case R.id.switchDepartureNotifications:
                areDepartureNotificationsEnabled = isChecked;
                break;
        }
    }

    private void loadCheckpointDetails(@NonNull TripCheckpoint tripCheckpoint) {
        if (isViewAttached) {
            view.displaySavedCheckpointDetails(tripCheckpoint);
        }
    }

    private void searchForCheckpointAddress(@NonNull LatLng newCheckpointLocation) {
        GpsLocationManager locationManager = GpsLocationManager.getInstance();
        locationManager.getAddressForLocation(newCheckpointLocation)
                .doOnNext(address -> {
                    if (isViewAttached) {
                        view.displaySearchedAddressForCheckpoint(address);
                    }
                    if (tripCheckpoint != null) {
                        tripCheckpoint.setCheckpointAddress(address);
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }
}

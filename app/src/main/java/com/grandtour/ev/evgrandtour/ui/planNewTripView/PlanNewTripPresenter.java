package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.data.network.NetworkRequestBuilders;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningCheckpointResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningTripResponse;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.DeleteInPlanningCheckpointUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.LoadInPlanningTripDetailsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.SaveInPlanningTripCheckpointUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.List;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter, OnSuccessListener<Location> {

    @NonNull
    private final PlanNewTripContract.View view;
    @Nullable
    private InPlanningTripDetails inPlanningTripDetails;

    private final static int USER_ID = 1;

    PlanNewTripPresenter(@NonNull PlanNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapReady() {
        addSubscription(new LoadInPlanningTripDetailsUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideCloudApi(), PlanNewTripPresenter.USER_ID).perform()
                .doOnNext(response -> {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        InPlanningTripResponse inPlanningTrip = response.body();
                        List<InPlanningCheckpointResponse> checkpoints = inPlanningTrip.getInPlanningCheckpoints();

                        inPlanningTripDetails = new InPlanningTripDetails(inPlanningTrip.getTripId());
                        inPlanningTripDetails.setInPlanningTripName(inPlanningTrip.getTripName());
                        inPlanningTripDetails.setInPlanningTripDescription(inPlanningTrip.getTripDescription());
                        if (checkpoints != null && checkpoints.size() > 0) {
                            inPlanningTripDetails.setPlannedTripCheckpoints(MapUtils.convertInPlanningCheckpointsResponseToMapObjects(checkpoints));
                        }
                        loadInPlanningTripOnMainMapView();
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe());
    }

    @Override
    public void onMapLocationClicked(@NonNull LatLng clickedLocation) {
        if (isViewAttached) {
            view.openNewCheckpointDetailsDialog(clickedLocation);
        }
    }

    @Override
    public void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint) {
        if (inPlanningTripDetails != null) {
            addSubscription(new SaveInPlanningTripCheckpointUseCase(Injection.provideRxSchedulers()
                    .getDefault(), Injection.provideCloudApi(), NetworkRequestBuilders.createPlannedTripCheckpointRequest(tripCheckpoint),
                    inPlanningTripDetails.getInPlanningTripId(), PlanNewTripPresenter.USER_ID).perform()
                    .subscribe(response -> {
                        if (response.code() == 200) {
                            if (response.body() != null && response.body() > 0 && isViewAttached) {
                                view.displayNewTripCheckpointOnMap(tripCheckpoint);
                            }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (isViewAttached) {
                            // TODO displayError message
                        }
                    }));
        }
    }

    @Override
    public void onDeleteCheckpointFromTrip(@NonNull TripCheckpoint tripCheckpoint) {
        if (inPlanningTripDetails != null)
            addSubscription(new DeleteInPlanningCheckpointUseCase(Injection.provideRxSchedulers()
                    .getDefault(), Injection.provideCloudApi(), inPlanningTripDetails.getInPlanningTripId(), tripCheckpoint.getCheckpointId()).perform()
                    .subscribe(response -> {
                        if (isViewAttached) {
                            if (response != null && response.body() != null && response.body() > 0) {
                                view.removeAddedTripCheckpoint(tripCheckpoint);
                            }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (isViewAttached) {
                            // TODO Add error handler
                        }
                    }));
    }

    @Override
    public void onMyLocationButtonClicked() {
        if (ActivityCompat.checkSelfPermission(Injection.provideGlobalContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            GpsLocationManager.getInstance()
                    .getLastKnownLocation(this);
        }
    }

    @Override
    public void onFinalizeTripPlanningClicked(@NonNull String tripTitle) {
        if (isViewAttached && inPlanningTripDetails != null) {
            if (TextUtils.isEmpty(tripTitle)) {
                view.displayInvalidTripNameWarning();
            } else if (inPlanningTripDetails.getPlannedTripCheckpoints()
                    .size() < 2) {
                view.showMessage(Injection.provideResources()
                        .getString(R.string.message_please_add_at_least_tqo_checkpoints_to_trip));
            } else {
                inPlanningTripDetails.setInPlanningTripName(tripTitle);
                //TODO Send final request confirming moving the trip to planned trips
            }
        }
    }

    private void loadInPlanningTripOnMainMapView() {
        if (isViewAttached && inPlanningTripDetails != null) {
            view.displayPlannedTripCheckpointsOnMapView(inPlanningTripDetails.getPlannedTripCheckpoints());
            view.displayPlannedTripNameAndDescription(inPlanningTripDetails.getInPlanningTripName(), inPlanningTripDetails.getInPlanningTripDescription());
        }
    }

    private void loadCheckpointsInReorderingList(@NonNull InPlanningTripDetails tripDetails) {
        if (isViewAttached) {
            view.displayTripCheckpointsInReorderingList(tripDetails.getPlannedTripCheckpoints());
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (isViewAttached) {
            view.moveCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }
}

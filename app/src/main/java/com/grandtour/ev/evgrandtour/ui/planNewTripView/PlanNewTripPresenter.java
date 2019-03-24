package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.data.network.NetworkRequestBuilders;
import com.grandtour.ev.evgrandtour.data.network.models.request.UpdateCheckpointLocationRequest;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningCheckpointResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningTripResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.useCases.newTripView.CalculateRouteDirectionsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.newTripView.DeleteInPlanningCheckpointUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.newTripView.FinalizeTripPlanningUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.newTripView.LoadInPlanningTripDetailsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.newTripView.SaveInPlanningTripCheckpointUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.newTripView.UpdateTripCheckpointLocationUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.TextUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import retrofit2.Response;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter, OnSuccessListener<Location> {

    private final static int USER_ID = 1;
    @NonNull
    private final PlanNewTripContract.View view;
    @Nullable
    private InPlanningTripDetails inPlanningTripDetails;

    PlanNewTripPresenter(@NonNull PlanNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapReady() {
        addSubscription(new LoadInPlanningTripDetailsUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideCloudApi(), PlanNewTripPresenter.USER_ID).perform()
                .doOnSubscribe(disposable -> {
                    if (isViewAttached()) {
                        view.showLoadingView(true);
                    }
                })
                .subscribe(response -> {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        onInPlanningTripResponseSuccess(response);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (isViewAttached()) {
                        view.showLoadingView(false);
                        view.showMessage(Injection.provideGlobalContext()
                                .getString(R.string.message_error_loading_checkpoints));
                    }
                }));
    }

    @Override
    public void onMapLocationClicked(@NonNull LatLng clickedLocation) {
        if (isViewAttached()) {
            view.openNewCheckpointDetailsDialog(clickedLocation);
        }
    }

    @Override
    public void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint) {
        if (inPlanningTripDetails != null) {
            if (isViewAttached()) {
                view.showLoadingView(true);
            }
            addSubscription(new SaveInPlanningTripCheckpointUseCase(Injection.provideRxSchedulers()
                    .getDefault(), Injection.provideCloudApi(),
                    NetworkRequestBuilders.createPlannedTripCheckpointRequest(tripCheckpoint, inPlanningTripDetails.getInPlanningTripId()),
                    PlanNewTripPresenter.USER_ID).perform()
                    .subscribe(response -> {
                        if (response.code() == 200) {
                            if (response.body() != null && response.body() > 0 && isViewAttached()) {
                                int addedCheckpointId = response.body();
                                tripCheckpoint.setCheckpointId(addedCheckpointId);
                                inPlanningTripDetails.getTripCheckpoints()
                                        .add(tripCheckpoint);
                                view.displayNewTripCheckpointOnMap(tripCheckpoint);

                                view.clearRoutePolyline();
                                startDirectionsRequest();
                            } else {
                                view.showLoadingView(false);
                                view.showMessage(Injection.provideGlobalContext()
                                        .getString(R.string.message_error_adding_checkpoint));
                            }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (isViewAttached()) {
                            view.showLoadingView(false);
                            view.showMessage(Injection.provideGlobalContext()
                                    .getString(R.string.message_error_adding_checkpoint));
                        }
                    }));
        }
    }

    @Override
    public void onDeleteCheckpointFromTrip(@NonNull TripCheckpoint tripCheckpoint) {
        if (inPlanningTripDetails != null) {
            if (isViewAttached()) {
                view.showLoadingView(true);
            }
        }
        addSubscription(new DeleteInPlanningCheckpointUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideCloudApi(), inPlanningTripDetails.getInPlanningTripId(), tripCheckpoint.getCheckpointId()).perform()
                .subscribe(response -> {
                    if (isViewAttached()) {
                        if (response != null && response.body() != null && response.body() > 0) {
                            view.removeAddedTripCheckpoint(tripCheckpoint);
                            inPlanningTripDetails.getTripCheckpoints()
                                    .remove(tripCheckpoint);

                            view.clearRoutePolyline();
                            startDirectionsRequest();
                        } else {
                            view.showLoadingView(false);
                            view.showMessage(Injection.provideGlobalContext()
                                    .getString(R.string.message_error_deleting_checkpoint));
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (isViewAttached()) {
                        view.showLoadingView(false);
                        view.showMessage(Injection.provideGlobalContext()
                                .getString(R.string.message_error_deleting_checkpoint));
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
        if (isViewAttached() && inPlanningTripDetails != null) {
            if (TextUtils.isEmpty(tripTitle)) {
                view.displayInvalidTripNameWarning();
            } else if (inPlanningTripDetails.getTripCheckpoints()
                    .size() < 2) {
                view.showMessage(Injection.provideResources()
                        .getString(R.string.message_please_add_at_least_tqo_checkpoints_to_trip));
            } else {
                addSubscription(new FinalizeTripPlanningUseCase(Injection.provideRxSchedulers()
                        .getDefault(), 1).perform()
                        .subscribe(response -> {
                           if(response.isSuccessful() && response.code() == 200){
                               onSavedInPlanningTripSuccessful();
                           }
                        }, Throwable::printStackTrace));
            }
        }
    }

    @Override
    public void onTripCheckpointLocationChanged(@NonNull Integer checkpointId, @NonNull LatLng updatedPosition) {
        UpdateCheckpointLocationRequest request = new UpdateCheckpointLocationRequest(checkpointId, updatedPosition.latitude, updatedPosition.longitude);
        addSubscription(new UpdateTripCheckpointLocationUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideCloudApi(), request, PlanNewTripPresenter.USER_ID).perform()
                .subscribe(response -> {
                    if (response.code() == 200 && response.body() != null) {
                        int updatedCheckpointId = response.body();
                        if (inPlanningTripDetails != null && isViewAttached()) {
                            List<TripCheckpoint> tripCheckpoints = inPlanningTripDetails.getTripCheckpoints();
                            for (TripCheckpoint checkpoint : tripCheckpoints) {
                                if (updatedCheckpointId == checkpoint.getCheckpointId()) {
                                    checkpoint.setGeographicalPosition(updatedPosition);
                                    view.clearRoutePolyline();
                                    view.showLoadingView(true);
                                    startDirectionsRequest();
                                }
                            }
                        }
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        view.showLoadingView(false);
                        view.showMessage(Injection.provideGlobalContext()
                                .getString(R.string.message_error_deleting_checkpoint));
                    }
                }));

    }

    private void onInPlanningTripResponseSuccess(@NonNull Response<InPlanningTripResponse> response) {
        InPlanningTripResponse inPlanningTrip = response.body();
        if (inPlanningTrip == null) {
            if (isViewAttached()) {
                view.showLoadingView(false);
            }
            return;
        }

        inPlanningTripDetails = new InPlanningTripDetails(inPlanningTrip.getTripId());
        inPlanningTripDetails.setInPlanningTripName(inPlanningTrip.getTripName());
        inPlanningTripDetails.setInPlanningTripDescription(inPlanningTrip.getTripDescription());
        loadInPlanningTripDetails();

        List<InPlanningCheckpointResponse> checkpoints = inPlanningTrip.getInPlanningCheckpoints();
        if (checkpoints != null && checkpoints.size() > 0) {
            inPlanningTripDetails.setTripCheckpoints(MapUtils.convertInPlanningCheckpointsResponseToMapObjects(checkpoints));
            loadInPlanningTripCheckpoints();
            view.clearRoutePolyline();
            startDirectionsRequest();

        } else if (isViewAttached()) {
            view.showLoadingView(false);
        }
    }

    private void onSavedInPlanningTripSuccessful(){
        if(isViewAttached()){
            view.moveBackToCurrentTripView();
        }
    }

    private void loadInPlanningTripDetails() {
        if (isViewAttached && inPlanningTripDetails != null) {
            view.displayPlannedTripNameAndDescription(inPlanningTripDetails.getInPlanningTripName(), inPlanningTripDetails.getInPlanningTripDescription());
        }
    }

    private void loadInPlanningTripCheckpoints() {
        if (isViewAttached() && inPlanningTripDetails != null) {
            List<TripCheckpoint> checkpoints = inPlanningTripDetails.getTripCheckpoints();
            if (checkpoints.size() > 0) {
                view.displayPlannedTripCheckpointsOnMapView(checkpoints);
                view.centerCameraOnCheckpoints(checkpoints);
            }
        }
    }

    private void startDirectionsRequest() {
        if (inPlanningTripDetails != null) {
            addSubscription(new CalculateRouteDirectionsUseCase(Injection.provideRxSchedulers()
                    .getDefault(), inPlanningTripDetails.getTripCheckpoints()).perform()
                    .doOnError(throwable -> {
                        throwable.printStackTrace();
                        if (isViewAttached) {
                            view.showLoadingView(false);
                            view.showMessage(Injection.provideGlobalContext()
                                    .getString(R.string.message_error_calculating_route));
                        }
                    })
                    .subscribe(responses -> {
                        if (isViewAttached) {
                            for (Response<RoutesResponse> response : responses) {
                                if (response.body() != null && response.code() == 200) {
                                    List<LatLng> routeCoordinates = MapUtils.convertPolyLineToMapPoints(response.body()
                                            .getRoutes()
                                            .get(0)
                                            .getOverviewPolyline()
                                            .getPoints());
                                    PolylineOptions routePolyline = MapUtils.generateRoute(routeCoordinates);
                                    if (isViewAttached) {
                                        view.drawRoutePolyline(routePolyline);
                                        view.showLoadingView(false);
                                    }
                                }
                            }
                        }
                    }));
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (isViewAttached) {
            view.moveCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }
}

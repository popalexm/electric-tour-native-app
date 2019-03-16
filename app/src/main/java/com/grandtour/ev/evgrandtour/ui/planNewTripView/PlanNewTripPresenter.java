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
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.CalculateRouteDirectionsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.DeleteInPlanningCheckpointUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.LoadInPlanningTripDetailsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.SaveInPlanningTripCheckpointUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.UpdateTripCheckpointLocationUseCase;
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
                    if (isViewAttached) {
                        view.showLoadingView(true);
                    }
                })
                .subscribe(response -> {
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
                        view.clearRoutePolyline();
                        startDirectionsRequest();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (isViewAttached) {
                        view.showLoadingView(false);
                        view.showMessage(Injection.provideGlobalContext()
                                .getString(R.string.message_error_loading_checkpoints));
                    }
                }));
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
            if (isViewAttached) {
                view.showLoadingView(true);
            }
            addSubscription(new SaveInPlanningTripCheckpointUseCase(Injection.provideRxSchedulers()
                    .getDefault(), Injection.provideCloudApi(), NetworkRequestBuilders.createPlannedTripCheckpointRequest(tripCheckpoint),
                    inPlanningTripDetails.getInPlanningTripId(), PlanNewTripPresenter.USER_ID).perform()
                    .subscribe(response -> {
                        if (response.code() == 200) {
                            if (response.body() != null && response.body() > 0 && isViewAttached) {
                                int addedCheckpointId = response.body();
                                tripCheckpoint.setCheckpointId(addedCheckpointId);
                                inPlanningTripDetails.getPlannedTripCheckpoints()
                                        .add(tripCheckpoint);
                                view.displayNewTripCheckpointOnMap(tripCheckpoint);

                                view.clearRoutePolyline();
                                startDirectionsRequest();
                            }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (isViewAttached) {
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
            if (isViewAttached) {
                view.showLoadingView(true);
            }
        }
        addSubscription(new DeleteInPlanningCheckpointUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideCloudApi(), inPlanningTripDetails.getInPlanningTripId(), tripCheckpoint.getCheckpointId()).perform()
                .subscribe(response -> {
                    if (isViewAttached) {
                        if (response != null && response.body() != null && response.body() > 0) {
                            view.removeAddedTripCheckpoint(tripCheckpoint);
                            inPlanningTripDetails.getPlannedTripCheckpoints()
                                    .remove(tripCheckpoint);

                            view.clearRoutePolyline();
                            startDirectionsRequest();
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (isViewAttached) {
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

    @Override
    public void onTripCheckpointLocationChanged(@NonNull Integer checkpointId, @NonNull LatLng updatedPosition) {
        UpdateCheckpointLocationRequest request = new UpdateCheckpointLocationRequest(checkpointId, updatedPosition.latitude, updatedPosition.longitude);
        addSubscription(new UpdateTripCheckpointLocationUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideCloudApi(), request, PlanNewTripPresenter.USER_ID).perform()
                .subscribe(response -> {
                    if (response.code() == 200 && response.body() != null) {
                        int updatedCheckpointId = response.body();
                        if (inPlanningTripDetails != null && isViewAttached) {
                            List<TripCheckpoint> tripCheckpoints = inPlanningTripDetails.getPlannedTripCheckpoints();
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
                    if (isViewAttached) {
                        view.showLoadingView(false);
                        view.showMessage(Injection.provideGlobalContext()
                                .getString(R.string.message_error_deleting_checkpoint));
                    }
                }));

    }

    private void loadInPlanningTripOnMainMapView() {
        if (isViewAttached && inPlanningTripDetails != null) {
            view.displayPlannedTripCheckpointsOnMapView(inPlanningTripDetails.getPlannedTripCheckpoints());
            view.displayPlannedTripNameAndDescription(inPlanningTripDetails.getInPlanningTripName(), inPlanningTripDetails.getInPlanningTripDescription());
            view.centerCameraOnCheckpoints(inPlanningTripDetails.getPlannedTripCheckpoints());
        }
    }

    private void startDirectionsRequest() {
        if (inPlanningTripDetails != null) {
            addSubscription(new CalculateRouteDirectionsUseCase(Injection.provideRxSchedulers()
                    .getDefault(), inPlanningTripDetails.getPlannedTripCheckpoints()).perform()
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

package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;
import com.grandtour.ev.evgrandtour.data.network.models.response.entireTour.ImportCheckpoint;
import com.grandtour.ev.evgrandtour.domain.useCases.CalculateTotalRoutesLengthUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.DeleteRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.DeleteStoredCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetAvailableRoutesUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetAvailableTourIDsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetAvailableToursUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetFollowingWaypointsFromOrigin;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadCheckpointMarkersForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadCheckpointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SaveCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SetTourSelectionStatusUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SyncAndSaveAllAvailableToursUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SyncAndSaveTourDetailsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.VerifyNumberOfAvailableRoutesUseCase;
import com.grandtour.ev.evgrandtour.services.RouteDirectionsRequestsService;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.utils.DocumentUtils;
import com.grandtour.ev.evgrandtour.ui.utils.JSONParsingUtils;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MapsFragmentPresenter extends BasePresenter implements MapsFragmentContract.Presenter, ServiceConnection {

    @NonNull
    private final String TAG = MapsFragmentPresenter.class.getSimpleName();
    @Nullable
    private Service routeDirectionsRequestService;
    @NonNull
    private final ServiceConnection serviceConnection = this;
    private boolean isServiceBound;
    @NonNull
    private final MapsFragmentContract.View view;
    @NonNull
    private final GpsLocationManager gpsLocationManager = GpsLocationManager.getInstance();

    MapsFragmentPresenter(@NonNull MapsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapReady() {
        if (isViewAttached) {
            reloadAvailableCheckpointsAndRoutes();
            startLocationUpdates();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(Injection.provideGlobalContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gpsLocationManager.stopRequestingLocationUpdates();
        }
    }

    @Override
    public void onUnBindDirectionsRequestService() {
        if (isServiceBound) {
            Injection.provideGlobalContext()
                    .unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    @Override
    public void onCalculatingRoutesStarted() {
        if (isViewAttached) {
            view.showLoadingView(true, true, Injection.provideGlobalContext()
                    .getString(R.string.message_calculating_routes));
        }
    }

    @Override
    public void onCalculatingRoutesDone() {
        if (isViewAttached) {
            view.showLoadingView(false, false, "");
        }
        reloadAvailableCheckpointsAndRoutes();
    }

    @Override
    public void onRoutesRequestsError(@NonNull String errorType) {
        if (TextUtils.equals(errorType, NetworkExceptions.UNKNOWN_HOST.name())) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.error_message_no_internet_connection));
        }
        if (TextUtils.equals(errorType, NetworkExceptions.STREAM_RESET_EXCEPTION.name())) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.error_message_internet_connection_intrerupted));
        }
        if (isViewAttached) {
            view.showLoadingView(false, false, "");
        }
    }

    @Override
    public void onCurrentLocationChanged(@NonNull Location location) {
        if (isViewAttached) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            view.updateCurrentUserLocation(latLng);
        }
    }

    @Override
    public void onLocalFileOpened(@NonNull Uri fileUri) {
        String json = "";
        try {
            json = DocumentUtils.readJSONFromUri(Injection.provideGlobalContext(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
            displayShortMessage(Injection.provideGlobalContext()
                    .getString(R.string.message_error_format_invalid));
        }
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new GsonBuilder().create();
            try {
                ImportCheckpoint[] checkpoints = gson.fromJson(json, ImportCheckpoint[].class);
                List<ImportCheckpoint> checkPointsFromJson = Arrays.asList(checkpoints);
                List<Checkpoint> toSaveCheckpoints = JSONParsingUtils.processImportedCheckpoints(checkPointsFromJson);
                saveCheckpoints(toSaveCheckpoints);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                displayShortMessage(Injection.provideGlobalContext()
                        .getString(R.string.message_error_opening_file));
            }
        }
    }

    @Override
    public void onClearCheckpointsAndRoutesClicked() {
        deleteAllCheckpointsAndRoutes();
    }

    @Override
    public void onCalculateRoutesClicked() {
        addSubscription(new VerifyNumberOfAvailableRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(numberOfAvailableRoutes -> {
                    if (numberOfAvailableRoutes > 0) {
                        view.showRouteReCalculationsDialog();
                    } else {
                        startRouteDirectionsRequests();
                    }
                }, Throwable::printStackTrace));
    }

    @Override
    public void onRecalculateRoutesConfirmation() {
        startRouteDirectionsRequests();
    }

    @Override
    public void onStopCalculatingRoutesClicked() {
        if (routeDirectionsRequestService != null) {
            routeDirectionsRequestService.stopSelf();
        }
        if (isViewAttached) {
            view.showLoadingView(false, false, "");
        }
        deletedAllStoredRoutes();
    }

    @Override
    public void onTotalRouteInfoClicked() {
        addSubscription(new CalculateTotalRoutesLengthUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(Integer -> {
                    int lengthInKm = Integer / 1000;
                    view.showTotalRouteLength(lengthInKm);
                }, Throwable::printStackTrace));
    }

    @Override
    public void onNavigationClicked(@NonNull Marker originMarker) {
        Integer checkpointId = (Integer) originMarker.getTag();
        if (checkpointId != null) {
            addSubscription(new GetFollowingWaypointsFromOrigin(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(),
                    checkpointId).perform()
                    .subscribe(checkpoints -> {
                        if (checkpoints.size() != 0) {
                            String navUri = MapUtils.composeUriForMapsIntentRequest(originMarker.getPosition(), checkpoints);
                            if (isViewAttached) {
                                view.startGoogleMapsDirections(navUri);
                            }
                        }
                    }));
        }
    }

    @Override
    public void onNewRoutesReceived(@NonNull ArrayList<LatLng> routeMapPoints) {
        drawRouteFromMapPoints(routeMapPoints);
    }

    @Override
    public void onCalculateDistanceBetweenTwoCheckpointsClicked() {
        addSubscription(new LoadCheckpointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(checkpoints -> {
                    if (checkpoints.size() != 0) {
                        view.showCalculateDistanceDialog(checkpoints);
                    }
                }));
    }

    @Override
    public void onChooseTourClicked() {
        SyncAndSaveAllAvailableToursUseCase syncAndSaveAllAvailableToursUseCase = new SyncAndSaveAllAvailableToursUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideBackendApi(), Injection.provideStorageManager());
        addSubscription(syncAndSaveAllAvailableToursUseCase.perform()
                .doOnSubscribe(disposable -> {
                    if (isViewAttached) {
                        view.showLoadingView(true, false, "Retrieving all available tours !");
                    }
                })
                .doOnSuccess(entireTourResponseResponse -> {
                    if (entireTourResponseResponse.length > 0) {
                        syncAllAvailableToursDetails();
                    }
                })
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    if (isViewAttached) {
                        view.showLoadingView(false, false, "");
                    }
                    displayShortMessage("An error occurred while retrieving the tours !");
                })
                .subscribe());
    }

    @Override
    public void onTourSelected(@NonNull String tourId) {
        new SetTourSelectionStatusUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), tourId).perform()
                .doOnComplete(this::startRouteDirectionsRequests)
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    private void syncAllAvailableToursDetails() {
        addSubscription(new GetAvailableTourIDsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnSuccess(tours -> {
                    new SyncAndSaveTourDetailsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideBackendApi(),
                            Injection.provideStorageManager(), tours).perform()
                            .doOnComplete(() -> {
                                if (isViewAttached) {
                                    view.showLoadingView(false, false, "");
                                }
                                openTourPickerDialog();
                            })
                            .doOnError(throwable -> {
                                throwable.printStackTrace();
                                if (isViewAttached) {
                                    view.showLoadingView(false, false, "");
                                }
                            })
                            .subscribe();
                })
                .subscribe());
    }

    private void openTourPickerDialog() {
        new GetAvailableToursUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnSuccess(view::showTourPickerDialog)
                .subscribe();
    }

    private void displayShortMessage(@NonNull String msg) {
        if (isViewAttached) {
            view.showMessage(msg);
        }
    }

    private void startRouteDirectionsRequests() {
        Context context = Injection.provideGlobalContext();
        Intent serviceIntent = new Intent(context, RouteDirectionsRequestsService.class);
        context.startService(serviceIntent);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;
    }

    private void saveCheckpoints(@NonNull List<Checkpoint> checkpoints) {
        LocalStorageManager storageManager = Injection.provideStorageManager();
        DeleteStoredCheckpointsUseCase deleteStoredCheckpointsUseCase = new DeleteStoredCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                storageManager);
        SaveCheckpointsUseCase saveCheckpointsUseCase = new SaveCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), storageManager,
                checkpoints);
        deleteStoredCheckpointsUseCase.perform()
                .andThen(saveCheckpointsUseCase.perform())
                .subscribe(new SingleObserver<long[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(long[] longs) {
                        Context ctx = Injection.provideGlobalContext();
                        String message = ctx.getString(R.string.format_start_number_end_message, ctx.getString(R.string.message_added), longs.length,
                                ctx.getString(R.string.message_checkpoints));
                        displayShortMessage(message);
                        loadAvailableCheckpoints();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void loadAvailableCheckpoints() {
        if (isViewAttached) {
            view.showLoadingView(true, false, Injection.provideGlobalContext()
                    .getString(R.string.message_loading_checkpoints));
        }
        addSubscription(new LoadCheckpointMarkersForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager()).perform()
                .subscribe(List -> {
                    if (isViewAttached) {
                        if (List.size() > 0) {
                            view.clearMapCheckpoints();
                            view.loadCheckpoints(List);
                        }
                        view.showLoadingView(false, false, "");
                    }

                }, Throwable::printStackTrace));
    }

    private void reloadAvailableCheckpointsAndRoutes() {
        if (isViewAttached) {
            view.showLoadingView(true, false, Injection.provideGlobalContext()
                    .getString(R.string.message_loading_available_checkpoints_and_routes));
            view.clearMapCheckpoints();
            view.clearMapRoutes();
        }
        Maybe<List<Route>> getAvailableRoutes = new GetAvailableRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager()).perform()
                .doOnSuccess(routes -> {
                    for (Route route : routes) {
                        List<LatLng> mapPoints = MapUtils.convertPolyLineToMapPoints(route.getRoutePolyline());
                        drawRouteFromMapPoints(mapPoints);
                    }
                })
                .doOnError(Throwable::printStackTrace);

        Maybe<List<Pair<Integer, MarkerOptions>>> getAvailableCheckpoints = new LoadCheckpointMarkersForSelectedTourUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnSuccess(checkpoints -> {
                    if (isViewAttached) {
                        if (checkpoints.size() > 0) {
                            view.loadCheckpoints(checkpoints);
                        }
                    }
                })
                .doOnError(Throwable::printStackTrace);
        Maybe.concat(getAvailableCheckpoints, getAvailableRoutes)
                .doOnComplete(() -> {
                    if (isViewAttached) {
                        view.showLoadingView(false, false, "");
                    }
                })
                .subscribe();
    }

    private void deleteAllCheckpointsAndRoutes() {
        DeleteRoutesUseCase deleteRoutesUseCase = new DeleteRoutesUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager());
        DeleteStoredCheckpointsUseCase deleteStoredCheckpointsUseCase = new DeleteStoredCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager());
        addSubscription(deleteStoredCheckpointsUseCase.perform()
                .andThen(deleteRoutesUseCase.perform())
                .subscribe(() -> {
                    if (isViewAttached) {
                        view.clearMapCheckpoints();
                        view.clearMapRoutes();
                    }
                }));
    }

    private void deletedAllStoredRoutes() {
        addSubscription(new DeleteStoredCheckpointsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .subscribe(() -> {
                    if (isViewAttached) {
                        view.clearMapRoutes();
                    }
                }));
    }

    private void drawRouteFromMapPoints(@NonNull List<LatLng> routeMapPoints) {
        PolylineOptions routePolyline = MapUtils.generateRoute(routeMapPoints);
        if (isViewAttached) {
            view.drawCheckpointsRoute(routePolyline);
        }
    }

    private void startLocationUpdates() {
        gpsLocationManager.initLocationClient();
        gpsLocationManager.createLocationRequest();
        gpsLocationManager.setCallback(new LocationUpdateCallback());
        if (ActivityCompat.checkSelfPermission(Injection.provideGlobalContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gpsLocationManager.startRequestingLocationUpdates();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        RouteDirectionsRequestsService.LocalBinder binder = (RouteDirectionsRequestsService.LocalBinder) service;
        routeDirectionsRequestService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        routeDirectionsRequestService = null;
    }

    private class LocationUpdateCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                Log.d(TAG, "Current user location is at  " + location.getLatitude() + "," + location.getLongitude());
                onCurrentLocationChanged(location);
            }
        }
    }

}

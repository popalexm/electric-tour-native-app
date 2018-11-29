package com.grandtour.ev.evgrandtour.services;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RouteResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.useCases.CalculateRouteUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.GetAvailableRouteLegsAndStepsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadCheckpointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.SaveRouteToDatabaseUseCase;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.http2.StreamResetException;

public class RouteDirectionsRequestsService extends Service {

    @NonNull
    public static final String ROUTE_START_REQUESTS_BUNDLE = "routeDirectionsRequestsStart";
    @NonNull
    public static final String REQUEST_ERROR_CODE = "requestErrorCode";
    @NonNull
    public static final String ACTION_ROUTE_BROADCAST = "RouteResultsBroadcast";
    @NonNull
    private final IBinder localBinder = new RouteDirectionsLocalBinder();
    @Nullable
    private Disposable directionsRequestsDisposable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestDirectionsForAvailableCheckpoints();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    private void requestDirectionsForAvailableCheckpoints() {
        Disposable disposable = new LoadCheckpointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(),
                Injection.provideStorageManager()).perform()
                .subscribe(this::startRouteDirectionsRequests);
    }

    private void startRouteDirectionsRequests(@NonNull List<Checkpoint> checkpoints) {
        directionsRequestsDisposable = new CalculateRouteUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), checkpoints, Injection.provideDirectionsApi(),
                Injection.provideStorageManager()).perform()
                .doOnComplete(() -> {
                    createElevationRequest();
                  /*  broadcastDirectionRequestProgress(false);
                    if (directionsRequestsDisposable != null) {
                        directionsRequestsDisposable.dispose();
                    }
                    stopSelf(); */
                })
                .doOnSubscribe(subscription -> {
                    broadcastDirectionRequestProgress(true);
                })
                .doOnError(throwable -> {
                    if (throwable instanceof UnknownHostException) {
                        broadcastRequestError(NetworkExceptions.UNKNOWN_HOST);
                    } else if (throwable instanceof StreamResetException) {
                        broadcastRequestError(NetworkExceptions.STREAM_RESET_EXCEPTION);
                    } else {
                        throwable.printStackTrace();
                    }
                    stopSelf();
                })
                .subscribe(response -> {
                    RoutesResponse routesResponse = response.body();
                    if (routesResponse != null) {
                        List<RouteResponse> routes = routesResponse.getRoutes();
                        if (routes != null && routes.size() > 0) {
                            RouteResponse route = routesResponse.getRoutes()
                                    .get(0);
                            saveRouteToDatabase(route);
                        }
                    }
                });
    }

    private void saveRouteToDatabase(@NonNull RouteResponse route) {
        new SaveRouteToDatabaseUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), route).perform()
                .subscribe();
    }

    private void createElevationRequest() {
        new GetAvailableRouteLegsAndStepsUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnSuccess(routeLegsSteps -> {
                    for (Pair<RouteLeg, List<RouteStep>> routeLegStepsPair : routeLegsSteps) {
                        List<RouteStep> steps = routeLegStepsPair.second;
                        int routeLegId = routeLegStepsPair.first.getRouteLegId();
                        List<LatLng> entireLegPolylineSteps = new ArrayList<>();
                        for (RouteStep routeStep : steps) {
                            List<LatLng> stepLinePoints = MapUtils.convertPolyLineToMapPoints(routeStep.getRouteStepPolyline());
                            entireLegPolylineSteps.addAll(stepLinePoints);
                        }
                        filterCheckpoints(entireLegPolylineSteps);
                    }
                    broadcastDirectionRequestProgress(false);
                    if (directionsRequestsDisposable != null) {
                        directionsRequestsDisposable.dispose();
                    }
                    stopSelf();

                })
                .subscribe();
    }

    /**
     * Takes a polyline array of LatLng points and filters them so that there is a distance
     */
    @NonNull
    private List<LatLng> filterCheckpoints(@NonNull List<LatLng> polylinePoints) {
        double maxCheckpointListDistanceLimit = 1000; // in Meters

        List<LatLng> filteredPoints = new ArrayList<>();
        double checkpointDistanceBuffer = 0;
        for (int i = 0; i < polylinePoints.size() - 1; i++) {

            LatLng firstCheckpoint = polylinePoints.get(i);
            LatLng secondCheckpoint = polylinePoints.get(i + 1);

            double distanceBetween = SphericalUtil.computeDistanceBetween(firstCheckpoint, secondCheckpoint);
            if (checkpointDistanceBuffer >= maxCheckpointListDistanceLimit) {
                filteredPoints.add(secondCheckpoint);
                checkpointDistanceBuffer = 0;
            } else {
                checkpointDistanceBuffer += distanceBetween;
            }
        }
        return filteredPoints;
    }

    private void broadcastDirectionRequestProgress(boolean areDirectionsRequestsInProgress) {
        Intent intent = new Intent(RouteDirectionsRequestsService.ACTION_ROUTE_BROADCAST);
        intent.putExtra(RouteDirectionsRequestsService.ROUTE_START_REQUESTS_BUNDLE, areDirectionsRequestsInProgress);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    private void broadcastRequestError(@NonNull NetworkExceptions exceptions) {
        Intent intent = new Intent(RouteDirectionsRequestsService.ACTION_ROUTE_BROADCAST);
        intent.putExtra(RouteDirectionsRequestsService.REQUEST_ERROR_CODE, exceptions.name());
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    public class RouteDirectionsLocalBinder extends Binder {

        public RouteDirectionsRequestsService getService() {
            return RouteDirectionsRequestsService.this;
        }
    }
}

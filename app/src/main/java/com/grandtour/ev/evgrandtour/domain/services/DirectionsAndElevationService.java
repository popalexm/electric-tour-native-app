package com.grandtour.ev.evgrandtour.domain.services;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.network.GoogleMapsAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;
import com.grandtour.ev.evgrandtour.data.network.NetworkRequestBuilders;
import com.grandtour.ev.evgrandtour.data.network.NetworkResponseConverter;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteDirectionsRequest;
import com.grandtour.ev.evgrandtour.data.network.models.response.elevation.ElevationResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.elevation.Result;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Leg;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RouteResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Step;
import com.grandtour.ev.evgrandtour.ui.utils.ArrayUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.annotation.Nullable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.http2.StreamResetException;
import retrofit2.Response;

public class DirectionsAndElevationService extends Service {

    @NonNull
    private static final String TAG = DirectionsAndElevationService.class.getSimpleName();

    private final static int NUMBER_OF_MAX_WAYPOINTS_PER_ROUTE_REQUEST = 12;
    @NonNull
    public static final String ROUTE_START_REQUESTS_BUNDLE = "routeDirectionsRequestsStart";
    @NonNull
    public static final String REQUEST_ERROR_CODE = "requestErrorCode";
    @NonNull
    public static final String ACTION_ROUTE_BROADCAST = "RouteResultsBroadcast";
    @NonNull
    private final IBinder localServiceBinder = new DirectionsAndElevationService.RouteDirectionsLocalBinder();
    @Nullable
    private Disposable directionsRequestsDisposable;

    @NonNull
    private final LocalStorageManager storageManager = Injection.provideStorageManager();
    @NonNull
    private final GoogleMapsAPI googleMapsAPI = Injection.provideDirectionsApi();
    @NonNull
    private final String googleApiKey = Injection.provideGlobalContext()
            .getResources()
            .getString(R.string.google_maps_key);

    public class RouteDirectionsLocalBinder extends Binder {

        public DirectionsAndElevationService getService() {
            return DirectionsAndElevationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestDirectionsForAvailableCheckpoints();
        return Service.START_NOT_STICKY;
    }

    private void requestDirectionsForAvailableCheckpoints() {
       Disposable disposable = loadCurrentTourCheckpoints()
                .doOnError(Throwable::printStackTrace)
                .subscribe(this::startRouteDirectionsRequests);
    }

    private void startRouteDirectionsRequests(@NonNull List<Checkpoint> checkpoints) {
        List<Maybe<Boolean>> routeUseCases = generateDirectionsRequests(checkpoints);

        directionsRequestsDisposable = Maybe.concat(routeUseCases)
                .doOnSubscribe(subscription -> {
                    broadcastDirectionRequestProgress(true);
                })
                .doOnComplete(() -> {
                    broadcastDirectionRequestProgress(false);
                    if (directionsRequestsDisposable != null) {
                        directionsRequestsDisposable.dispose();
                    }
                    stopSelf();
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
                .subscribe();
    }

    @NonNull
    private Maybe<List<Checkpoint>> loadCurrentTourCheckpoints() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Function<String, MaybeSource<List<Checkpoint>>>) tourId -> storageManager.checkpointsDao()
                        .getAllCheckpointsForTourId(tourId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io()));
    }

    @NonNull
    private List<Maybe<Boolean>> generateDirectionsRequests(@NonNull List<Checkpoint> checkpoints) {

        List<Maybe<Boolean>> routeDirectionsApiCallList = new ArrayList<>();

        List<List<Checkpoint>> totalBatchedRouteRequests = ArrayUtils.splitCheckpointsIntoBatches(checkpoints,
                DirectionsAndElevationService.NUMBER_OF_MAX_WAYPOINTS_PER_ROUTE_REQUEST);

        for (int i = 0; i < totalBatchedRouteRequests.size(); i++) {

            List<Checkpoint> routeRequestCheckpointList = totalBatchedRouteRequests.get(i);
            RouteDirectionsRequest routeDirectionsRequest = NetworkRequestBuilders.generateDirectionRequestParameters(routeRequestCheckpointList, googleApiKey);

            Maybe<Boolean> routeDirectionsApiCall = requestDirectionsAPICall(routeDirectionsRequest)
                    .flatMap(new Function<Response<RoutesResponse>, MaybeSource<List<Long>>>() {
                        @Override
                        public MaybeSource<List<Long>> apply(Response<RoutesResponse> directionsApiResponse) {
                            RoutesResponse routeResponseBody = directionsApiResponse.body();
                            RouteResponse routeResponse = routeResponseBody.getRoutes().get(0);
                            return saveRouteResponseAndGenerateElevationPoints(routeResponse, routeRequestCheckpointList);
                        }
                    })
                    .flatMap(new Function<List<Long>, MaybeSource<Boolean>>() {
                        @Override
                        public MaybeSource<Boolean> apply(List<Long> routeLegIdList) {
                            return requestElevationDataForRouteLegs(routeLegIdList);
                        }
                    })
                    .doOnError(Throwable::printStackTrace);
            routeDirectionsApiCallList.add(routeDirectionsApiCall);
        }
        return routeDirectionsApiCallList;
    }

    @NonNull
    public Maybe<Response<RoutesResponse>> requestDirectionsAPICall(@NonNull RouteDirectionsRequest routeDirectionsRequest) {
        return googleMapsAPI.getDirectionsForWaypoints(routeDirectionsRequest.startWaypoint, routeDirectionsRequest.endWaypoint,
                routeDirectionsRequest.transitWaypoints, routeDirectionsRequest.apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    /** Handles route response caching inside the local app database
     *  @return Returns a list of routeLegIds (primaryKeys) resulted from the route response caching
     */

    @NonNull
    public Maybe<List<Long>> saveRouteResponseAndGenerateElevationPoints(@NonNull RouteResponse routeResponse, @NonNull List<Checkpoint> routeRequestCheckpoints) {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, MaybeSource<List<Long>>>() {
                    @Override
                    public MaybeSource<List<Long>> apply(String tourId) {
                        return Maybe.fromCallable(() -> {
                            String routePolyline = routeResponse.getOverviewPolyline().getPoints();

                            Route route = NetworkResponseConverter.convertResponseToRoute(routePolyline, tourId);

                            long routeId = storageManager.routeDao()
                                    .insert(route);
                            List<Leg> responseLegs = routeResponse.getLegs();

                            List<Long> routeLegIds = new ArrayList<>();

                            for (int i = 0; i < responseLegs.size(); i++) {
                                Leg legResponse = responseLegs.get(i);
                                Pair<Long, List<LatLng>> routeAndElevationPoints = insertNewRoutesAndLegsInDatabase(legResponse, (int) routeId);
                                Long routeLegId = routeAndElevationPoints.first;
                                routeLegIds.add(routeLegId);

                                int checkpointId = routeRequestCheckpoints.get(i)
                                        .getCheckpointId();
                                updateDistanceDurationToNextData(legResponse, checkpointId);

                                List<ElevationPoint> elevationPoints = NetworkResponseConverter.convertCoordinatesToElevationPoints(routeLegId,
                                        routeAndElevationPoints.second);
                                storageManager.elevationPointDao()
                                        .insert(elevationPoints);

                            }
                            return routeLegIds;
                        });
                    }
                });
    }

    /**Saves the legs response and associated steps
     */
    private Pair<Long, List<LatLng>> insertNewRoutesAndLegsInDatabase(@NonNull Leg legResponse, int routeId) {
        RouteLeg routeLeg = NetworkResponseConverter.convertResponseToRouteLeg(legResponse, routeId);
        long routeLegId = storageManager.routeLegDao()
                .insertRouteLeg(routeLeg);

        List<Step> responseSteps = legResponse.getSteps();
        List<RouteStep> routeStepsList = new ArrayList<>();

        List<LatLng> routeLegElevationPoints = new ArrayList<>();
        for (Step stepResponse : responseSteps) {
            RouteStep routeStep = NetworkResponseConverter.convertResponseToRouteStep(stepResponse, (int) routeLegId);
            routeStepsList.add(routeStep);
            List<LatLng> responseStepPoints = PolyUtil.decode(stepResponse.getPolyline()
                    .getPoints());
            List<LatLng> stepElevationPoints = filterPolylineForEachKilometerOfCheckpoints(responseStepPoints);

            routeLegElevationPoints.addAll(stepElevationPoints);
        }
        storageManager.routeStepDao()
                .insertRouteLeg(routeStepsList);
        return new Pair<>(routeLegId, routeLegElevationPoints);
    }

    /** Updates the distanceTo and durationTo fields for
     * a specific route Checkpoint
     */
    private void updateDistanceDurationToNextData(@NonNull Leg responseLeg, int checkpointId ){
        int distanceToNext = responseLeg
                .getDistance()
                .getValue();
        int durationToNext = responseLeg
                .getDuration()
                .getValue();
        storageManager.checkpointsDao()
                .updateCheckpointById(checkpointId, distanceToNext, durationToNext);
    }

    /**
     * Takes an array of LatLng points extracted from a Polyline
     * and filters them so that there is a distance of 1Km / 1000m between each point
     * This method is used to generate the Elevation points for each Leg of the Route
     */
    @NonNull
    private List<LatLng> filterPolylineForEachKilometerOfCheckpoints(@NonNull List<LatLng> polylinePoints) {
        double maxDistanceBetweenPoints = 2000; // in Meters
        List<LatLng> filteredPolylinePoints = new ArrayList<>();

        double checkpointDistanceBuffer = 0;
        for (int i = 0; i < polylinePoints.size() - 1; i++) {

            LatLng firstCheckpoint = polylinePoints.get(i);
            LatLng secondCheckpoint = polylinePoints.get(i + 1);

            double distanceBetweenPoints = SphericalUtil.computeDistanceBetween(firstCheckpoint, secondCheckpoint);
            if (checkpointDistanceBuffer >= maxDistanceBetweenPoints) {
                filteredPolylinePoints.add(secondCheckpoint);
                checkpointDistanceBuffer = 0;
            } else {
                checkpointDistanceBuffer += distanceBetweenPoints;
            }
        }
        return filteredPolylinePoints;
    }

    @NonNull
    public Maybe<Boolean> requestElevationDataForRouteLegs(@NonNull List<Long> routeLegIds) {
        return Maybe.fromCallable(new Callable<List<ElevationPoint>>() {
            @Override
            public List<ElevationPoint> call() {
                return storageManager.elevationPointDao()
                        .getElevationPointsForRouteLegIds(routeLegIds);
            }
        }).flatMap(new Function<List<ElevationPoint>, Maybe<Boolean>>() {
                    @Override
                    public Maybe<Boolean> apply(List<ElevationPoint> elevationPoints) {
                        return createElevationApiRequest(elevationPoints, googleApiKey);
                    }
        })
        .doOnError(Throwable::printStackTrace)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io());
    }

    @NonNull
    private Maybe<Boolean> createElevationApiRequest(@NonNull List<ElevationPoint> elevationPoints, @NonNull String apiKey) {
        String elevationRequestString = NetworkRequestBuilders.createElevationRequest(elevationPoints);
        return googleMapsAPI.getElevationForCheckpoints(elevationRequestString, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .flatMap(new Function<Response<ElevationResponse>, MaybeSource<Boolean>>() {
                    @Override
                    public MaybeSource<Boolean> apply(Response<ElevationResponse> elevationResponse) {
                        return Maybe.fromCallable(() -> {
                            ElevationResponse elevationApiResponseBody = elevationResponse.body();

                            if (elevationApiResponseBody != null) {
                                List<Result> elevationResponseResultsList = elevationApiResponseBody.getResults();
                                for (int index = 0; index < elevationPoints.size(); index++) {

                                    int elevationPointId = elevationPoints.get(index)
                                            .getElevationPointId();
                                    double elevation = elevationResponseResultsList.get(index)
                                            .getElevation();

                                    storageManager.elevationPointDao()
                                            .updateElevationPoint(elevationPointId, elevation);
                                }
                                return true;
                            } else {
                                return false;
                            }
                        });
                    }
                });
    }

    private void broadcastDirectionRequestProgress(boolean areDirectionsRequestsInProgress) {
        Intent intent = new Intent(DirectionsAndElevationService.ACTION_ROUTE_BROADCAST);
        intent.putExtra(DirectionsAndElevationService.ROUTE_START_REQUESTS_BUNDLE, areDirectionsRequestsInProgress);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    private void broadcastRequestError(@NonNull NetworkExceptions exceptions) {
        Intent intent = new Intent(DirectionsAndElevationService.ACTION_ROUTE_BROADCAST);
        intent.putExtra(DirectionsAndElevationService.REQUEST_ERROR_CODE, exceptions.name());
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }
}

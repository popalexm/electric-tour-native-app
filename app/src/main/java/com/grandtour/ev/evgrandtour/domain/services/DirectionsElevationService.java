package com.grandtour.ev.evgrandtour.domain.services;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

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

public class DirectionsElevationService extends Service {

    @NonNull
    private static final String TAG = DirectionsElevationService.class.getSimpleName();

    private final static int NUMBER_OF_MAX_CHECKPOINTS_PER_DIRECTIONS_REQUEST = 12;

    @NonNull
    private final IBinder localServiceBinder = new DirectionsElevationService.RouteDirectionsLocalBinder();
    @NonNull
    private final LocalStorageManager storageManager = Injection.provideStorageManager();
    @NonNull
    private final GoogleMapsAPI googleMapsAPI = Injection.provideDirectionsApi();
    @NonNull
    private final String googleApiKey = Injection.provideGlobalContext()
            .getResources()
            .getString(R.string.google_maps_key);
    @Nullable
    private Disposable directionsRequestsDisposable;

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
        requestDirectionsAndElevationForSelectedTour();
        return Service.START_NOT_STICKY;
    }

    private void requestDirectionsAndElevationForSelectedTour() {
       Disposable disposable = loadCurrentTourCheckpoints()
                .doOnError(Throwable::printStackTrace)
                .subscribe(this::startRouteDirectionsRequests);
    }

    private void startRouteDirectionsRequests(@NonNull List<Checkpoint> checkpoints) {
        List<Maybe<Boolean>> routeDirectionsRequestCalls = generateDirectionsRequestsList(checkpoints);

        directionsRequestsDisposable = Maybe.concat(routeDirectionsRequestCalls)
                .doOnSubscribe(subscription -> {
                    ServiceStatusBroadcastManager.getInstance()
                            .broadcastDirectionRequestProgress(true);
                })
                .doOnComplete(() -> {
                    ServiceStatusBroadcastManager.getInstance()
                            .broadcastDirectionRequestProgress(false);
                    if (directionsRequestsDisposable != null) {
                        directionsRequestsDisposable.dispose();
                    }
                    stopSelf();
                })
                .doOnError(throwable -> {
                    ServiceStatusBroadcastManager broadcastManager = ServiceStatusBroadcastManager.getInstance();
                    if (throwable instanceof UnknownHostException) {
                        broadcastManager.broadcastRequestError(NetworkExceptions.UNKNOWN_HOST);
                    } else if (throwable instanceof StreamResetException) {
                        broadcastManager.broadcastRequestError(NetworkExceptions.STREAM_RESET_EXCEPTION);
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

    /**
     * Generates a list of API request observables for the selected checkpoints, bacthes of a maximum of
     * 12 checkpoints per request , since this is a limitation of the Google Directions API
     */
    @NonNull
    private List<Maybe<Boolean>> generateDirectionsRequestsList(@NonNull List<Checkpoint> checkpoints) {

        List<Maybe<Boolean>> routeDirectionsApiCallList = new ArrayList<>();

        List<List<Checkpoint>> totalBatchedRouteRequests = ArrayUtils.splitCheckpointsIntoBatches(checkpoints,
                DirectionsElevationService.NUMBER_OF_MAX_CHECKPOINTS_PER_DIRECTIONS_REQUEST);

        for (int i = 0; i < totalBatchedRouteRequests.size(); i++) {

            List<Checkpoint> routeRequestCheckpointList = totalBatchedRouteRequests.get(i);
            RouteDirectionsRequest routeDirectionsRequest = NetworkRequestBuilders.generateDirectionRequestParameters(routeRequestCheckpointList, googleApiKey);

            Maybe<Boolean> routeDirectionsApiCall = requestDirectionsAPICall(routeDirectionsRequest).flatMap(new Function<Response<RoutesResponse>, MaybeSource<List<Long>>>() {
                @Override
                public MaybeSource<List<Long>> apply(Response<RoutesResponse> directionsApiResponse) {
                    RoutesResponse routeResponseBody = directionsApiResponse.body();
                    RouteResponse routeResponse = routeResponseBody.getRoutes().get(0);
                    return saveDirectionsAPIResponseAndGenerateElevationPoints(routeResponse, routeRequestCheckpointList);
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
    public Maybe<List<Long>> saveDirectionsAPIResponseAndGenerateElevationPoints(@NonNull RouteResponse routeResponse,
            @NonNull List<Checkpoint> routeRequestCheckpoints) {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, MaybeSource<List<Long>>>() {
                    @Override
                    public MaybeSource<List<Long>> apply(String tourId) {
                        return Maybe.fromCallable(() -> {
                            String routePolyline = routeResponse.getOverviewPolyline().getPoints();

                            Route route = NetworkResponseConverter.convertToRouteDatabaseModel(routePolyline, tourId);
                            long routeId = storageManager.routeDao()
                                    .insert(route);

                            List<Leg> responseLegs = routeResponse.getLegs();

                            List<Long> insertedRouteLegIds = new ArrayList<>();

                            for (int i = 0; i < responseLegs.size(); i++) {
                                Leg legResponse = responseLegs.get(i);

                                int routeStartCheckpointId = routeRequestCheckpoints.get(i)
                                        .getCheckpointId();
                                int routeEndCheckpointId = routeRequestCheckpoints.get(i + 1)
                                        .getCheckpointId();
                                Pair<Long, List<LatLng>> routeLegIdAndElevationLatLngPoints = insertNewRoutesAndLegsInDatabase(legResponse, (int) routeId,
                                        routeStartCheckpointId, routeEndCheckpointId);

                                int checkpointId = routeRequestCheckpoints.get(i)
                                        .getCheckpointId();

                                Long routeLegId = routeLegIdAndElevationLatLngPoints.first;
                                List<LatLng> elevationLatLngList = routeLegIdAndElevationLatLngPoints.second;

                                insertedRouteLegIds.add(routeLegId);
                                updateDistanceAndDurationForCheckpointId(legResponse, checkpointId);

                                int checkpointOrderInTourId = routeRequestCheckpoints.get(i)
                                        .getOrderInTourId();
                                insertElevationPointsToDatabase(routeLegId, checkpointOrderInTourId, elevationLatLngList);
                            }
                            return insertedRouteLegIds;
                        });
                    }
                })
                .doOnError(Throwable::printStackTrace);
    }

    /**Saves the legs response and associated steps
     * @return returns a
     */
    private Pair<Long, List<LatLng>> insertNewRoutesAndLegsInDatabase(@NonNull Leg legResponse, int routeId, int routeStartCheckpointId,
            int routeEndCheckpointId) {
        RouteLeg routeLeg = NetworkResponseConverter.convertResponseToRouteLeg(legResponse, routeId, routeStartCheckpointId, routeEndCheckpointId);
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
            List<LatLng> stepElevationPoints = DirectionsElevationServiceUtils.extractElevationPointsFromPolyline(responseStepPoints);

            routeLegElevationPoints.addAll(stepElevationPoints);
        }
        storageManager.routeStepDao()
                .insertRouteLeg(routeStepsList);
        return new Pair<>(routeLegId, routeLegElevationPoints);
    }

    /**
     * Generates new elevation points database models from a list of LatLng points
     * and saves them to the local db
     *
     * @param routeLegId            associated with the route
     * @param elevationLatLngPoints List of LatLng coordinates that need to be converted to database models and saved
     */
    private void insertElevationPointsToDatabase(long routeLegId, int checkpointOrderId, @NonNull Iterable<LatLng> elevationLatLngPoints) {
        List<ElevationPoint> elevationPoints = NetworkResponseConverter.convertCoordinatesToElevationPoints(routeLegId, elevationLatLngPoints,
                checkpointOrderId);
        storageManager.elevationPointDao()
                .insert(elevationPoints);
    }

    /** Updates the distanceTo and durationTo fields for
     * a specific route Checkpoint
     */
    private void updateDistanceAndDurationForCheckpointId(@NonNull Leg responseLeg, int checkpointId ){
        int distanceToNext = responseLeg
                .getDistance()
                .getValue();
        int durationToNext = responseLeg
                .getDuration()
                .getValue();
        storageManager.checkpointsDao()
                .updateCheckpointById(checkpointId, distanceToNext, durationToNext);
    }

    /** Creates a API request on the Google Elevation API for a list of routeLeg ids
     */
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

    /** Creates and API request for the selected Elevation Points and updates the selected room entities
     * with the response
     */
    @NonNull
    private Maybe<Boolean> createElevationApiRequest(@NonNull List<ElevationPoint> apiRequestElevationPoints, @NonNull String apiKey) {
        String elevationRequestString = NetworkRequestBuilders.createElevationRequest(apiRequestElevationPoints);
        return googleMapsAPI.getElevationForCheckpoints(elevationRequestString, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .flatMap(new Function<Response<ElevationResponse>, MaybeSource<Boolean>>() {
                    @Override
                    public MaybeSource<Boolean> apply(Response<ElevationResponse> elevationApiResponse) {
                        return Maybe.fromCallable(() -> {
                            ElevationResponse elevationApiResponseBody = elevationApiResponse.body();

                            if (elevationApiResponseBody != null) {
                                List<Result> elevationResponseResultsList = elevationApiResponseBody.getResults();
                                for (int index = 0; index < apiRequestElevationPoints.size(); index++) {

                                    int elevationPointId = apiRequestElevationPoints.get(index)
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

    public class RouteDirectionsLocalBinder extends Binder {

        public DirectionsElevationService getService() {
            return DirectionsElevationService.this;
        }
    }

}

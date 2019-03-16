package com.grandtour.ev.evgrandtour.domain.useCases.newTripView;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.GoogleMapsAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkRequestBuilders;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteDirectionsRequest;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.utils.ArrayUtils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import retrofit2.Response;

public class CalculateRouteDirectionsUseCase extends BaseUseCaseRefactored {

    private final static int NUMBER_OF_MAX_CHECKPOINTS_PER_DIRECTIONS_REQUEST = 12;

    @NonNull
    private final GoogleMapsAPI googleMapsAPI = Injection.provideDirectionsApi();
    @NonNull
    private final String googleApiKey = Injection.provideGlobalContext()
            .getResources()
            .getString(R.string.google_maps_key);
    @NonNull
    private final List<TripCheckpoint> checkpoints;

    public CalculateRouteDirectionsUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull List<TripCheckpoint> checkpoints) {
        super(schedulers);
        this.checkpoints = checkpoints;
    }

    @Override
    public Single<List<Response<RoutesResponse>>> perform() {
        List<Observable<Response<RoutesResponse>>> requests = generateDirectionsRequestsList(checkpoints);
        return Observable.concat(requests)
                .toList()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }

    /**
     * Generates a list of API request observables for the selected checkpoints, bacthes of a maximum of
     * 12 checkpoints per request , since this is a limitation of the Google Directions API
     */
    @NonNull
    private List<Observable<Response<RoutesResponse>>> generateDirectionsRequestsList(@NonNull List<TripCheckpoint> checkpoints) {

        List<Observable<Response<RoutesResponse>>> checkpointsDirectionsApiCalls = new ArrayList<>();
        //* Splits the total checkpoints list into batches of requests for 12 checkpoints per each
        List<List<TripCheckpoint>> totalBatchedRouteRequests = ArrayUtils.splitCheckpointsIntoBatches(checkpoints,
                CalculateRouteDirectionsUseCase.NUMBER_OF_MAX_CHECKPOINTS_PER_DIRECTIONS_REQUEST);

        for (int i = 0; i < totalBatchedRouteRequests.size(); i++) {

            List<TripCheckpoint> routeRequestCheckpointList = totalBatchedRouteRequests.get(i);
            RouteDirectionsRequest routeDirectionsRequest = NetworkRequestBuilders.createDirectionRequestParameters(routeRequestCheckpointList, googleApiKey);

            Observable<Response<RoutesResponse>> routeDirectionsApiCall = requestDirectionsBetweenCheckpoints(routeDirectionsRequest);
            checkpointsDirectionsApiCalls.add(routeDirectionsApiCall);
        }
        return checkpointsDirectionsApiCalls;
    }

    @NonNull
    private Observable<Response<RoutesResponse>> requestDirectionsBetweenCheckpoints(@NonNull RouteDirectionsRequest routeDirectionsRequest) {
        return googleMapsAPI.getDirectionsForWaypointsList(routeDirectionsRequest.startWaypoint, routeDirectionsRequest.endWaypoint,
                routeDirectionsRequest.transitWaypoints, routeDirectionsRequest.apiKey)
                .subscribeOn(executorThread)
                .observeOn(executorThread);
    }
}

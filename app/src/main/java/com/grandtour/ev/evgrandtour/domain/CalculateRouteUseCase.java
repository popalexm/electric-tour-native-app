package com.grandtour.ev.evgrandtour.domain;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.data.network.NetworkAPI;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteParameters;
import com.grandtour.ev.evgrandtour.data.network.models.response.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseFlowable;
import com.grandtour.ev.evgrandtour.utils.ArrayUtils;
import com.grandtour.ev.evgrandtour.utils.MapUtils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class CalculateRouteUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    @NonNull
    private final List<Marker> waypoints;
    @NonNull
    private final NetworkAPI networkAPI;
    private final static int NUMBER_OF_MAX_WAYPOINTS_PER_ROUTE = 12;

    public CalculateRouteUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull NetworkAPI networkAPI,
            @NonNull List<Marker> waypoints) {
        super(executorThread, postExecutionThread);
        this.waypoints = waypoints;
        this.networkAPI = networkAPI;
    }

    @Override
    public Flowable<Response<RoutesResponse>> perform() {
        List<Maybe<Response<RoutesResponse>>> routeUseCases = generateIndividualRouteUseCases(waypoints);
        return Maybe.concat(routeUseCases);
    }


    @NonNull
    private List<Maybe<Response<RoutesResponse>>> generateIndividualRouteUseCases(@NonNull List<Marker> waypoints) {
        List<Maybe<Response<RoutesResponse>>> calculateRouteTasks = new ArrayList<>();
        List<List<Marker>> routesList = ArrayUtils.split(waypoints, CalculateRouteUseCase.NUMBER_OF_MAX_WAYPOINTS_PER_ROUTE);
        for (int i = 0; i < routesList.size(); i ++) {
            List<Marker> route = routesList.get(i);
            Marker startMarker = route.get(0);
            Marker endMarker = route.get(route.size() - 1);
            List<LatLng> transitWaypoints = new ArrayList<>();
            for (int j = 1 ; j < route.size() - 2; j++) {
                Marker transitWaypoint = route.get(j);
                transitWaypoints.add(transitWaypoint.getPosition());
            }
            RouteParameters routeParameters = MapUtils.generateRouteParams(startMarker.getPosition() , endMarker.getPosition(), transitWaypoints);
            Maybe<Response<RoutesResponse>> calculateRouteUseCase  = new RequestDirectionsUseCase(executorThread, postExecutionThread, networkAPI, routeParameters).perform();
            calculateRouteTasks.add(calculateRouteUseCase);
        }
        return calculateRouteTasks;
    }
}

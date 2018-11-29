package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.network.GoogleMapsAPI;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteParameters;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class RequestDirectionsUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final GoogleMapsAPI googleMapsAPI;
    @NonNull
    private final RouteParameters routeParameters;

    RequestDirectionsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull GoogleMapsAPI googleMapsAPI,
            @NonNull RouteParameters routeParameters) {
        super(executorThread, postExecutionThread);
        this.googleMapsAPI = googleMapsAPI;
        this.routeParameters = routeParameters;
    }

    @Override
    public Maybe<Response<RoutesResponse>> perform() {
        return googleMapsAPI.getDirectionsForWaypoints(routeParameters.startWaypoint, routeParameters.endWaypoint, routeParameters.transitWaypoints,
                routeParameters.apiKey)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

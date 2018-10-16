package com.grandtour.ev.evgrandtour.domain.useCases;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.DirectionsAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkStatusCodes;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteParameters;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseFlowable;
import com.grandtour.ev.evgrandtour.domain.handlers.RoutesResponseHandler;
import com.grandtour.ev.evgrandtour.ui.utils.ArrayUtils;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class CalculateRouteUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    private final static int NUMBER_OF_MAX_WAYPOINTS_PER_ROUTE_REQUEST = 12;
    @NonNull
    private final List<Checkpoint> checkpoints;
    @NonNull
    private final DirectionsAPI directionsAPI;
    @NonNull
    private final LocalStorageManager storageManager;

    public CalculateRouteUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull List<Checkpoint> checkpoints,
            @NonNull DirectionsAPI directionsAPI, @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.checkpoints = checkpoints;
        this.storageManager = storageManager;
        this.directionsAPI = directionsAPI;
    }

    @Override
    public Flowable<Response<RoutesResponse>> perform() {
        List<Maybe<Response<RoutesResponse>>> routeUseCases = generateIndividualRouteUseCases(checkpoints);
        return Maybe.concat(routeUseCases);
    }

    @NonNull
    private List<Maybe<Response<RoutesResponse>>> generateIndividualRouteUseCases(@NonNull List<Checkpoint> checkpoints) {
        List<Maybe<Response<RoutesResponse>>> calculateRouteTasks = new ArrayList<>();
        List<List<Checkpoint>> totalBatchedRouteRequests = ArrayUtils.splitCheckpointsIntoBatches(checkpoints,
                CalculateRouteUseCase.NUMBER_OF_MAX_WAYPOINTS_PER_ROUTE_REQUEST);

        for (int i = 0; i < totalBatchedRouteRequests.size(); i++) {
            List<Checkpoint> singleRouteRequestBatch = totalBatchedRouteRequests.get(i);
            List<LatLng> checkpointCoordinateList = new ArrayList<>();

            for (int j = 0; j < singleRouteRequestBatch.size(); j++) {
                Checkpoint checkpoint = singleRouteRequestBatch.get(j);
                checkpointCoordinateList.add(new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude()));
            }

            RouteParameters routeParameters = MapUtils.generateRouteRequestParams(checkpointCoordinateList);
            Maybe<Response<RoutesResponse>> calculateRouteUseCase = new RequestDirectionsUseCase(executorThread, postExecutionThread, directionsAPI,
                    routeParameters).perform()
                    .doOnSuccess(response -> {
                        if (response != null) {
                            if (response.code() == NetworkStatusCodes.NETWORK_REPONSE_CODE_OK) {
                                RoutesResponse responseBody = response.body();
                                if (responseBody != null) {
                                    new RoutesResponseHandler(storageManager).handleRouteResponse(responseBody, singleRouteRequestBatch);
                                }
                            }
                        }
                    })
                    .doOnError(Throwable::printStackTrace);

            calculateRouteTasks.add(calculateRouteUseCase);
        }
        return calculateRouteTasks;
    }
}

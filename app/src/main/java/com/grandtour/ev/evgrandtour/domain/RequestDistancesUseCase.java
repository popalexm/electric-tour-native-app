package com.grandtour.ev.evgrandtour.domain;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.data.network.NetworkAPI;
import com.grandtour.ev.evgrandtour.data.network.models.request.DistanceRequest;
import com.grandtour.ev.evgrandtour.data.network.models.response.distance.DistanceResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseFlowable;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class RequestDistancesUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    @NonNull
    private final NetworkAPI networkAPI;
    @NonNull
    private final List<Marker> checkpoints;

    public RequestDistancesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull NetworkAPI networkAPI,
            @NonNull List<Marker> checkpoints) {
        super(executorThread, postExecutionThread);
        this.networkAPI = networkAPI;
        this.checkpoints = checkpoints;
    }

    @Override
    public Flowable<Response<DistanceResponse>> perform() {
        List<Maybe<Response<DistanceResponse>>> distanceRequestCalls = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < checkpoints.size() - 1; currentIndex++) {
            int nextIndex = currentIndex + 1;
            LatLng startCheckpointCoordinates = checkpoints.get(currentIndex)
                    .getPosition();
            LatLng endCheckpointCoordinates = checkpoints.get(nextIndex)
                    .getPosition();
            DistanceRequest distanceRequest = MapUtils.generateDistanceRequest(startCheckpointCoordinates, endCheckpointCoordinates);
            Maybe<Response<DistanceResponse>> distanceRequestCall = networkAPI.getDistanceBetweenPoints(distanceRequest.startpoint, distanceRequest.endWaypoint,
                    distanceRequest.apiKey)
                    .subscribeOn(executorThread)
                    .observeOn(postExecutionThread);
            distanceRequestCalls.add(distanceRequestCall);
        }
        return Maybe.concat(distanceRequestCalls);
    }
}

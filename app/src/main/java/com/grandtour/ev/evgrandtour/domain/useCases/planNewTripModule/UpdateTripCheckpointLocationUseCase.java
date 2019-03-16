package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.network.HolidayTripCloudAPI;
import com.grandtour.ev.evgrandtour.data.network.models.request.UpdateCheckpointLocationRequest;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;

import android.util.Pair;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class UpdateTripCheckpointLocationUseCase extends BaseUseCaseRefactored {

    @NonNull
    private final HolidayTripCloudAPI cloudAPI;
    @NonNull
    private final UpdateCheckpointLocationRequest locationRequest;
    @NonNull
    private final Integer userId;

    public UpdateTripCheckpointLocationUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull HolidayTripCloudAPI cloudAPI,
            @NonNull UpdateCheckpointLocationRequest locationRequest, @NonNull Integer userId) {
        super(schedulers);
        this.cloudAPI = cloudAPI;
        this.locationRequest = locationRequest;
        this.userId = userId;
    }

    @Override
    public Observable<Response<Integer>> perform() {
        return cloudAPI.updatePlannedCheckpointLocationForTripId(userId, locationRequest)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

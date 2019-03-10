package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.network.HolidayTripCloudAPI;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class DeleteInPlanningCheckpointUseCase extends BaseUseCaseRefactored {

    private final int checkpointId;
    private final int tripId;
    @NonNull
    private final HolidayTripCloudAPI cloudAPI;

    public DeleteInPlanningCheckpointUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull HolidayTripCloudAPI cloudAPI, int tripId,
            int checkpointId) {
        super(schedulers);
        this.checkpointId = checkpointId;
        this.tripId = tripId;
        this.cloudAPI = cloudAPI;
    }

    @Override
    public Observable<Response<Integer>> perform() {
        return cloudAPI.deletePlannedCheckpointForTripId(tripId, checkpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

package com.grandtour.ev.evgrandtour.domain.useCases.newTripView;

import com.grandtour.ev.evgrandtour.data.network.HolidayTripCloudAPI;
import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedCheckpointRequest;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;

import android.util.Pair;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class SaveInPlanningTripCheckpointUseCase extends BaseUseCaseRefactored {

    @NonNull
    private final HolidayTripCloudAPI cloudAPI;
    @NonNull
    private final PlannedCheckpointRequest checkpointRequest;
    private final int userId;

    public SaveInPlanningTripCheckpointUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull HolidayTripCloudAPI cloudAPI,
            @NonNull PlannedCheckpointRequest checkpointRequest, int userId) {
        super(schedulers);
        this.cloudAPI = cloudAPI;
        this.checkpointRequest = checkpointRequest;
        this.userId = userId;
    }

    @Override
    public Observable<Response<Integer>> perform() {
        return cloudAPI.postPlannedCheckpoint(checkpointRequest)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

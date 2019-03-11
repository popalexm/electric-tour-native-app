package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.network.HolidayTripCloudAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningTripResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class LoadInPlanningTripDetailsUseCase extends BaseUseCaseRefactored {

    @NonNull
    private final HolidayTripCloudAPI cloudAPI;
    private final int userId;

    public LoadInPlanningTripDetailsUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull HolidayTripCloudAPI cloudApi, int userId) {
        super(schedulers);
        this.cloudAPI = cloudApi;
        this.userId = userId;
    }

    @Override
    public Observable<Response<InPlanningTripResponse>> perform() {
        return cloudAPI.getCurrentInPlanningTripForUser(userId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

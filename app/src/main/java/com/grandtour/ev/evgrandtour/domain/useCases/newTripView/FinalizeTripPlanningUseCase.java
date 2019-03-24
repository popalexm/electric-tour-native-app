package com.grandtour.ev.evgrandtour.domain.useCases.newTripView;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.HolidayTripCloudAPI;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;

import android.util.Pair;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class FinalizeTripPlanningUseCase extends BaseUseCaseRefactored {

    @NonNull
    private final HolidayTripCloudAPI cloudAPI;
    @NonNull
    private final Integer userId;

    public FinalizeTripPlanningUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull Integer userId) {
        super(schedulers);
        this.userId = userId;
        cloudAPI = Injection.provideCloudApi();
    }

    @Override
    public Observable<Response<Integer>> perform() {
        return cloudAPI.setInPlanningTripAsDone(userId).subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

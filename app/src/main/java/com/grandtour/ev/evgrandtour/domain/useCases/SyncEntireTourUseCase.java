package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.entireTour.EntireTourResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class SyncEntireTourUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final BackendAPI backendAPI;

    public SyncEntireTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI) {
        super(executorThread, postExecutionThread);
        this.backendAPI = backendAPI;
    }

    @Override
    public Maybe<Response<EntireTourResponse>> perform() {
        return backendAPI.getEntireTourCheckpoints().subscribeOn(executorThread).observeOn(postExecutionThread);
    }
}

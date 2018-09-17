package com.grandtour.ev.evgrandtour.domain;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWithWaypoints;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;

public class GetAvailableRoutesUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public GetAvailableRoutesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<RouteWithWaypoints>> perform() {
        return storageManager.routeDao()
                .getAllRoutesAddWaypoints()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

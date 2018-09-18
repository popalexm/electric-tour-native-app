package com.grandtour.ev.evgrandtour.domain;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class DeletePreviousWaypointsUseCase extends BaseUseCase implements BaseUseCaseCompletable {

    @NonNull
    private final LocalStorageManager storageManager;


    public DeletePreviousWaypointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> storageManager.waypointsDao()
                .deleteAll())
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

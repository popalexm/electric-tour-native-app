package com.grandtour.ev.evgrandtour.domain;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;

import android.support.annotation.NonNull;
import android.util.Log;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;

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
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                storageManager.waypointsDao().deleteAllStoredWaypoints();
            }
        }).subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

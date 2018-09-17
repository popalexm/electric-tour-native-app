package com.grandtour.ev.evgrandtour.domain;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;

public class DeleteRoutesUseCase extends BaseUseCase implements BaseUseCaseCompletable {

    @NonNull
    private final LocalStorageManager localStorageManager;

    public DeleteRoutesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager localStorageManager) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() {
                localStorageManager.routeWaypointsDao()
                        .deleteAll();
                localStorageManager.routeDao()
                        .deleteAll();
            }
        })
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

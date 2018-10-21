package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class DeleteRoutesUseCase extends BaseUseCase implements BaseUseCaseCompletable {

    @NonNull
    private final LocalStorageManager localStorageManager;

    public DeleteRoutesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager localStorageManager) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> {
            localStorageManager.routeDao()
                    .deleteAll();
        })
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

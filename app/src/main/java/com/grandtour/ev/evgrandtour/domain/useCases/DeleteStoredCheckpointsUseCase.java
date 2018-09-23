package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class DeleteStoredCheckpointsUseCase extends BaseUseCase implements BaseUseCaseCompletable {

    @NonNull
    private final LocalStorageManager storageManager;

    public DeleteStoredCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> storageManager.checkpointsDao()
                .deleteAll())
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

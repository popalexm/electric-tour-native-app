package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class DeleteInPlanningCheckpointUseCase extends BaseUseCaseRefactored {

    private final int checkpointId;
    @NonNull
    private final LocalStorageManager storageManager;

    public DeleteInPlanningCheckpointUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull LocalStorageManager storageManager, int checkpointId) {
        super(schedulers);
        this.checkpointId = checkpointId;
        this.storageManager = storageManager;
    }

    @Override
    public Completable perform() {
        return storageManager.inPlanningTripCheckpointDao()
                .deleteInPlanningCheckpoint(checkpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

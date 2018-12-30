package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class LoadNextCheckpointsFromOriginPoint extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    private final int checkpointId;
    private final int maxCheckpointsToRetrieve;
    private final int startCheckpointId;
    private final int endCheckpointId;

    public LoadNextCheckpointsFromOriginPoint(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, int checkpointId, int maxCheckpointsToRetrieve, int startCheckpointId, int endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.checkpointId = checkpointId;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
        this.maxCheckpointsToRetrieve = maxCheckpointsToRetrieve;
    }

    @Override
    public Single<List<Checkpoint>> perform() {
        return storageManager.checkpointsDao()
                .getNextCheckpointsFromOrigin(checkpointId, maxCheckpointsToRetrieve, startCheckpointId, endCheckpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

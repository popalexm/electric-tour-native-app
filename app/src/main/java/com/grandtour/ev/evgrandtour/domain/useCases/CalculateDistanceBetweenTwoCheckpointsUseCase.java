package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class CalculateDistanceBetweenTwoCheckpointsUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final Integer startCheckpointId;
    @NonNull
    private final Integer endCheckpointId;

    public CalculateDistanceBetweenTwoCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, @NonNull Integer startCheckpointId, @NonNull Integer endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
    }

    @Override
    public Single<Integer> perform() {
        return storageManager.checkpointsDao()
                .getDistanceBetweenTwoCheckpoints(startCheckpointId, endCheckpointId);
    }
}

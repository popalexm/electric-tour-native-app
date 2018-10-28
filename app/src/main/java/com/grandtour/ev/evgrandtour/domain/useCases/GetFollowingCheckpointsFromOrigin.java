package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class GetFollowingCheckpointsFromOrigin extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    private final int checkpointId;

    public GetFollowingCheckpointsFromOrigin(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, int checkpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.checkpointId = checkpointId;
    }

    @Override
    public Single<List<Checkpoint>> perform() {
        return storageManager.checkpointsDao().getNextTenCheckpoints(checkpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;
import com.grandtour.ev.evgrandtour.ui.currentTripView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadMapCheckpointsForFilteredCheckpointsUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager storageManager;
    private final int startCheckpointId;
    private final int endCheckpointId;

    public LoadMapCheckpointsForFilteredCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, int startCheckpointId, int endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
    }

    @Override
    public Maybe<List<MapCheckpoint>> perform() {
        return storageManager.checkpointsDao()
                .getAllCheckpointsBetweenStartAndEndCheckpointIds(startCheckpointId, endCheckpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap((Function<List<Checkpoint>, MaybeSource<List<MapCheckpoint>>>) checkpoints -> Maybe.fromCallable(
                        () -> MapUtils.convertToMapCheckpoints(checkpoints)));
    }
}

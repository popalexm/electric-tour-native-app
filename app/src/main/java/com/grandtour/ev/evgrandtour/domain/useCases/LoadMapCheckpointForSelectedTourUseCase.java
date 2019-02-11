package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;
import com.grandtour.ev.evgrandtour.ui.currentTripView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadMapCheckpointForSelectedTourUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadMapCheckpointForSelectedTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<MapCheckpoint>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap((Function<String, MaybeSource<List<MapCheckpoint>>>) tourId -> storageManager.checkpointsDao()
                        .getAllCheckpointsForTourId(tourId)
                        .flatMap((Function<List<Checkpoint>, MaybeSource<List<MapCheckpoint>>>) checkpoints -> Maybe.fromCallable(
                                () -> MapUtils.convertToMapCheckpoints(checkpoints)))
                        .subscribeOn(executorThread)
                        .observeOn(postExecutionThread));
    }
}

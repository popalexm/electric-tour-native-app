package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadCheckpointsForSelectedTourUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadCheckpointsForSelectedTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<Checkpoint>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap((Function<String, MaybeSource<List<Checkpoint>>>) tourId -> storageManager.checkpointsDao()
                        .getAllCheckpointsForTourId(tourId)
                        .subscribeOn(executorThread)
                        .observeOn(postExecutionThread));
    }
}

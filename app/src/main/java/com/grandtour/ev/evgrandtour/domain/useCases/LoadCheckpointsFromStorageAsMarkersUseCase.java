package com.grandtour.ev.evgrandtour.domain.useCases;

import com.google.android.gms.maps.model.MarkerOptions;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadCheckpointsFromStorageAsMarkersUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadCheckpointsFromStorageAsMarkersUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<Pair<Integer, MarkerOptions>>> perform() {
        return storageManager.checkpointsDao()
                .getAllCheckpoints()
                .flatMap((Function<List<Checkpoint>, MaybeSource<List<Pair<Integer, MarkerOptions>>>>) checkpoints -> Maybe.fromCallable(
                        () -> MapUtils.convertCheckpointsToMarkers(checkpoints)))
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

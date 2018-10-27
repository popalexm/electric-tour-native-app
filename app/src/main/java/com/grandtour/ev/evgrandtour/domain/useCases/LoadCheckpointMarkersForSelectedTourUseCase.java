package com.grandtour.ev.evgrandtour.domain.useCases;

import com.google.android.gms.maps.model.MarkerOptions;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
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

public class LoadCheckpointMarkersForSelectedTourUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadCheckpointMarkersForSelectedTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<Pair<Integer, MarkerOptions>>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap((Function<String, MaybeSource<List<Pair<Integer, MarkerOptions>>>>) tourId -> storageManager.checkpointsDao()
                        .getAllCheckpointsForTourId(tourId)
                        .flatMap((Function<List<Checkpoint>, MaybeSource<List<Pair<Integer, MarkerOptions>>>>) checkpoints -> Maybe.fromCallable(
                                () -> MapUtils.convertCheckpointsToMarkers(checkpoints)))
                        .subscribeOn(executorThread)
                        .observeOn(postExecutionThread));
    }
}

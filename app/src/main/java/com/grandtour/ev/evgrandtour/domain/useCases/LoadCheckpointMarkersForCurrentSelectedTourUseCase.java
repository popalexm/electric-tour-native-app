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

public class LoadCheckpointMarkersForCurrentSelectedTourUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadCheckpointMarkersForCurrentSelectedTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<Pair<Integer, MarkerOptions>>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<String, MaybeSource<List<Pair<Integer, MarkerOptions>>>>() {
                    @Override
                    public MaybeSource<List<Pair<Integer, MarkerOptions>>> apply(String tourId) {
                        return storageManager.checkpointsDao()
                                .getAllCheckpointsForTourId(tourId)
                                .flatMap((Function<List<Checkpoint>, MaybeSource<List<Pair<Integer, MarkerOptions>>>>) checkpoints -> Maybe.fromCallable(
                                        () -> MapUtils.convertCheckpointsToMarkers(checkpoints)))
                                .subscribeOn(executorThread)
                                .observeOn(postExecutionThread);
                    }
                });
    }
}

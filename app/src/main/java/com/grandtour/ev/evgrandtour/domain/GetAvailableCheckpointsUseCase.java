package com.grandtour.ev.evgrandtour.domain;

import com.google.android.gms.maps.model.MarkerOptions;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class GetAvailableCheckpointsUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public GetAvailableCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<MarkerOptions>> perform() {
        return storageManager.checkpointsDao()
                .getAllCheckpoints()
                .flatMap((Function<List<Checkpoint>, MaybeSource<List<MarkerOptions>>>) checkpoints -> Maybe.fromCallable(
                        () -> MapUtils.convertWaypointsToMarkers(checkpoints)))
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

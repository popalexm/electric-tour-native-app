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

public class QueryForRoutesUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final String queryText;

    public QueryForRoutesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager,
            @NonNull String queryText) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.queryText = queryText;
    }

    @Override
    public Maybe<List<Checkpoint>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .flatMap((Function<String, MaybeSource<List<Checkpoint>>>) tourId -> storageManager.checkpointsDao()
                        .getCheckpointsThatMatchQuery("%" + queryText + "%", tourId));
    }
}

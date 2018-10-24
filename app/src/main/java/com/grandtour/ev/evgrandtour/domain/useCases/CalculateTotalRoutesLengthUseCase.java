package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.functions.BiFunction;

public class CalculateTotalRoutesLengthUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public CalculateTotalRoutesLengthUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<Pair<Integer, Integer>> perform() {
        Maybe<Integer> distance = storageManager.checkpointsDao()
                .getTotalDistanceForTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        Maybe<Integer> duration = storageManager.checkpointsDao()
                .getTotalRouteTimeForTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        return Maybe.zip(distance, duration, new BiFunction<Integer, Integer, Pair<Integer, Integer>>() {
            @Override
            public Pair<Integer, Integer> apply(Integer distance, Integer duration) {
                return new Pair<>(distance, duration);
            }
        });
    }
}

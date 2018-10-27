package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function3;

public class CalculateTotalRoutesLengthUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public CalculateTotalRoutesLengthUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<Pair<Pair<Integer, Integer>, String>> perform() {
        Maybe<Integer> distance = storageManager.checkpointsDao()
                .getTotalDistanceForTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        Maybe<Integer> duration = storageManager.checkpointsDao()
                .getTotalRouteTimeForTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        Maybe<String> selectedRouteName = storageManager.tourDao()
                .getCurrentlySelectedTourName()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        return Maybe.zip(distance, duration, selectedRouteName, new Function3<Integer, Integer, String, Pair<Pair<Integer, Integer>, String>>() {
            @Override
            public Pair<Pair<Integer, Integer>, String> apply(Integer distance, Integer duration, String routeTitle) {
                Pair<Integer, Integer> distanceDurationPair = new Pair<>(distance, duration);
                return new Pair<>(distanceDurationPair, routeTitle);
            }
        });
    }
}

package com.grandtour.ev.evgrandtour.domain.useCases.currentTripView;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function3;

public class LoadRouteInformationUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager storageManager;
    @Nullable
    private final Integer startCheckpointId;
    @Nullable
    private final Integer endCheckpointId;

    public LoadRouteInformationUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager,
            @Nullable Integer startCheckpointId, @Nullable Integer endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
    }

    @Override
    public Maybe<Pair<Pair<Integer, Integer>, String>> perform() {
        if (startCheckpointId != null && endCheckpointId != null) {
            Maybe<Integer> distance = storageManager.checkpointsDao()
                    .getDistanceBetweenTwoCheckpoints(startCheckpointId, endCheckpointId)
                    .subscribeOn(executorThread)
                    .observeOn(postExecutionThread);
            Maybe<Integer> duration = storageManager.checkpointsDao()
                    .getDrivingTimeBetweenTwoCheckpoints(startCheckpointId, endCheckpointId)
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
        } else {
            Maybe<Integer> distance = storageManager.checkpointsDao()
                    .getDistanceForEntireTour()
                    .subscribeOn(executorThread)
                    .observeOn(postExecutionThread);
            Maybe<Integer> duration = storageManager.checkpointsDao()
                    .getDrivingTimeForEntireTour()
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
}

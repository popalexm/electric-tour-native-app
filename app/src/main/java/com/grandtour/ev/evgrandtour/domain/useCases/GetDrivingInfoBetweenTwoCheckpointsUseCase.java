package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;

public class GetDrivingInfoBetweenTwoCheckpointsUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final Integer startCheckpointId;
    @NonNull
    private final Integer endCheckpointId;

    public GetDrivingInfoBetweenTwoCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, @NonNull Integer startCheckpointId, @NonNull Integer endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
    }

    @Override
    public Single<Pair<Integer, Integer>> perform() {
        Single<Integer> distance = storageManager.checkpointsDao()
                .getDistanceBetweenTwoCheckpoints(startCheckpointId, endCheckpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        Single<Integer> duration = storageManager.checkpointsDao()
                .getDrivingTimeBetweenTwoCheckpoints(startCheckpointId, endCheckpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
        return Single.zip(distance, duration, new BiFunction<Integer, Integer, Pair<Integer, Integer>>() {
            @Override
            public Pair<Integer, Integer> apply(Integer distance, Integer duration) {
                return new Pair<>(distance, duration);
            }
        });
    }
}

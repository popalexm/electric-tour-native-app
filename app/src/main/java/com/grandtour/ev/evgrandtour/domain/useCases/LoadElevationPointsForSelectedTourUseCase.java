package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;

public class LoadElevationPointsForSelectedTourUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager localStorageManager;
    @Nullable
    private final Integer startCheckpointId;
    @Nullable
    private final Integer endCheckpointId;

    public LoadElevationPointsForSelectedTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @Nullable Integer startCheckpointId, @Nullable Integer endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
    }

    @Override
    public Maybe<List<ElevationPoint>> perform() {
        if (startCheckpointId != null && endCheckpointId != null) {
            return localStorageManager.elevationPointDao()
                    .getElevationPointsBetweenTwoCheckpoints(startCheckpointId, endCheckpointId);
        } else {
            return localStorageManager.elevationPointDao()
                    .getAllElevationPoints();
        }
    }
}

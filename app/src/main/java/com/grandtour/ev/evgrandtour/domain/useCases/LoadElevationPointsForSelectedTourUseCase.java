package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;

public class LoadElevationPointsForSelectedTourUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager localStorageManager;

    public LoadElevationPointsForSelectedTourUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
    }

    @Override
    public Maybe<List<ElevationPoint>> perform() {
        return localStorageManager.elevationPointDao()
                .getAllElevationPoints();
    }
}

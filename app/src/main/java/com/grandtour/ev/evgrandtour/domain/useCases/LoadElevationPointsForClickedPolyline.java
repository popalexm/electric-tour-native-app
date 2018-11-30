package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;

public class LoadElevationPointsForClickedPolyline extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager localStorageManager;
    @NonNull
    private final Integer routeLegId;

    public LoadElevationPointsForClickedPolyline(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull Integer routeLegId) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
        this.routeLegId = routeLegId;
    }

    @Override
    public Maybe<List<ElevationPoint>> perform() {
        return localStorageManager.elevationPointDao()
                .getElevationPointsForRouteLegId(routeLegId);
    }
}

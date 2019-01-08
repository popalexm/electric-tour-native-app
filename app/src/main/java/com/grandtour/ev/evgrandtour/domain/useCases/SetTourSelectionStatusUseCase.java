package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class SetTourSelectionStatusUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager localStorageManager;
    @NonNull
    private final String tourId;

    public SetTourSelectionStatusUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull String tourId) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
        this.tourId = tourId;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> localStorageManager.tourDao()
                .updateTourSelectionById(tourId, 1));
    }
}

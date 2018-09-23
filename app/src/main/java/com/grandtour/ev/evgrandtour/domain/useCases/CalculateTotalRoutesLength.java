package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;

public class CalculateTotalRoutesLength extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public CalculateTotalRoutesLength(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<Integer> perform() {
        return storageManager.checkpointsDao()
                .getAllDistances()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

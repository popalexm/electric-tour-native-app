package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class VerifyNumberOfAvailableRoutesUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;

    public VerifyNumberOfAvailableRoutesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Single<Integer> perform() {
        return storageManager.routeDao()
                .getTotalAvailableRoutes()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

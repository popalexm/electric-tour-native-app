package com.grandtour.ev.evgrandtour.domain.base;

import androidx.annotation.NonNull;
import io.reactivex.Scheduler;


public abstract class BaseUseCase {

    @NonNull
    public final Scheduler executorThread;
    @NonNull
    public final Scheduler postExecutionThread;

    protected BaseUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread) {
        this.executorThread = executorThread;
        this.postExecutionThread = postExecutionThread;
    }
}

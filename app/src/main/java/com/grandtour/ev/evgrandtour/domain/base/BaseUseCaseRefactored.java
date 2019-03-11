package com.grandtour.ev.evgrandtour.domain.base;

import android.util.Pair;

import androidx.annotation.NonNull;
import io.reactivex.Scheduler;

public abstract class BaseUseCaseRefactored implements UseCaseDefinition {

    @NonNull
    public final Scheduler executorThread;
    @NonNull
    public final Scheduler postExecutionThread;

    public BaseUseCaseRefactored(@NonNull Pair<Scheduler, Scheduler> schedulers) {
        this.executorThread = schedulers.first;
        this.postExecutionThread = schedulers.second;
    }
}

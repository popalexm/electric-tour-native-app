package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.InPlanningTrip;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadInPlanningTripUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadInPlanningTripUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Observable<InPlanningTrip> perform() {
        return Observable.fromCallable(() -> storageManager.inPlanningTripDao()
                .checkNumberOfTableRows())
                .flatMap((Function<Integer, ObservableSource<InPlanningTrip>>) numberOfInPlanningTrips -> {
                    if (numberOfInPlanningTrips == 0) {
                        // If there is no table for inPlanninTrips , create one
                        return Observable.fromCallable(() -> {
                            long insertedTripId = storageManager.inPlanningTripDao()
                                    .insert(new InPlanningTrip());
                            return storageManager.inPlanningTripDao()
                                    .getInPlanningTripById(insertedTripId);
                        });
                    } else {
                        // Else return the previously created table fields
                        return Observable.fromCallable(() -> storageManager.inPlanningTripDao()
                                .getLastInPlanningTrip());
                    }
                })
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

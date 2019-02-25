package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;
import com.grandtour.ev.evgrandtour.domain.models.PlannedTripStatus;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class MoveInPlanningTripToPlannedStatus extends BaseUseCaseRefactored {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final InPlanningTripDetails inPlanningTripDetails;

    public MoveInPlanningTripToPlannedStatus(@NonNull Pair<Scheduler, Scheduler> schedulers, @NonNull LocalStorageManager storageManager,
            @NonNull InPlanningTripDetails inPlanningTripDetails) {
        super(schedulers);
        this.storageManager = storageManager;
        this.inPlanningTripDetails = inPlanningTripDetails;
    }

    @Override
    public Observable<PlannedTripStatus> perform() {
        return Observable.fromCallable(() -> storageManager.inPlanningTripDao()
                .updateTripNameAndDescription(inPlanningTripDetails.getInPlanningTripName(), inPlanningTripDetails.getInPlanningTripDescription(),
                        inPlanningTripDetails.getInPlanningTripId()))
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<Integer, ObservableSource<PlannedTripStatus>>() {
                    @Override
                    public ObservableSource<PlannedTripStatus> apply(Integer integer) {
                        // TODO Add moving the trip to the proper tables after updating values, and then returning the value

                        return Observable.fromCallable(() -> PlannedTripStatus.STATUS_SAVED_LOCALLY_SUCCESSFULLY);
                    }
                });
    }
}

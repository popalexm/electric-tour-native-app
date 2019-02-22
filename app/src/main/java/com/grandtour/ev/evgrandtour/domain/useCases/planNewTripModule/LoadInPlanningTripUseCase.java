package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.InPlanningCheckpoint;
import com.grandtour.ev.evgrandtour.data.database.models.InPlanningTrip;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;
import com.grandtour.ev.evgrandtour.domain.useCases.modelConversion.ModelConversionUtils;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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
    public Observable<InPlanningTripDetails> perform() {
        return Observable.fromCallable(() -> storageManager.inPlanningTripDao()
                .checkNumberOfTableRows())
                .flatMap((Function<Integer, ObservableSource<InPlanningTripDetails>>) numberOfInPlanningTrips -> {
                    if (numberOfInPlanningTrips == 0) {
                        // If there is no table for inPlanningTrips , create one
                        return Observable.fromCallable(() -> {
                            storageManager.inPlanningTripDao()
                                    .insert(new InPlanningTrip());
                            return retrieveCurrentInPlanningTripData();
                        });
                    } else {
                        // Else return the previously created table fields
                        return Observable.fromCallable(this::retrieveCurrentInPlanningTripData);
                    }
                })
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);

    }

    @NonNull
    private InPlanningTripDetails retrieveCurrentInPlanningTripData() {
        InPlanningTrip inPlanningTrip = storageManager.inPlanningTripDao()
                .getLastInPlanningTrip();
        int inPlanningTripId = inPlanningTrip.getTripId();

        List<InPlanningCheckpoint> inPlanningCheckpoints = storageManager.inPlanningTripCheckpointDao()
                .getInPlanningCheckpointsByTripId(inPlanningTripId);

        InPlanningTripDetails inPlanningTripDetails = new InPlanningTripDetails(inPlanningTripId);
        inPlanningTripDetails.setInPlanningTripName(inPlanningTrip.getTripName());
        inPlanningTripDetails.setInPlanningTripDescription(inPlanningTrip.getTripDescription());

        List<TripCheckpoint> savedInPlanningCheckpoints = new ArrayList<>();
        for (InPlanningCheckpoint checkpoint : inPlanningCheckpoints) {
            TripCheckpoint tripCheckpoint = ModelConversionUtils.convertDatabaseModelMapCheckpointModel(checkpoint);
            savedInPlanningCheckpoints.add(tripCheckpoint);
        }
        inPlanningTripDetails.setPlannedTripCheckpoints(savedInPlanningCheckpoints);

        return inPlanningTripDetails;
    }
}

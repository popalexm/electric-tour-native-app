package com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.InPlanningCheckpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseRefactored;
import com.grandtour.ev.evgrandtour.domain.useCases.modelConversion.ModelConversionUtils;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class SaveInPlanningTripCheckpointUseCase extends BaseUseCaseRefactored {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final TripCheckpoint tripCheckpoint;
    private final int tripId;

    public SaveInPlanningTripCheckpointUseCase(@NonNull Pair<Scheduler, Scheduler> schedulers,
            @NonNull LocalStorageManager storageManager, @NonNull TripCheckpoint tripCheckpoint, int tripId) {
        super(schedulers);
        this.storageManager = storageManager;
        this.tripCheckpoint = tripCheckpoint;
        this.tripId = tripId;
    }

    @Override
    public Observable<TripCheckpoint> perform() {
        return Observable.fromCallable(() -> {
            InPlanningCheckpoint inPlanningCheckpoint = ModelConversionUtils.convertMapCheckpointModelToDatabaseModel(tripCheckpoint, tripId);
            long checkpointId = storageManager.inPlanningTripCheckpointDao()
                    .insert(inPlanningCheckpoint);
            InPlanningCheckpoint insertedCheckpoint = storageManager.inPlanningTripCheckpointDao()
                    .getInPlanningCheckpointById(checkpointId);
            return ModelConversionUtils.convertDatabaseModelMapCheckpointModel(insertedCheckpoint);
        });
    }
}

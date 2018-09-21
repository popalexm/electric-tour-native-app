package com.grandtour.ev.evgrandtour.domain;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;

public class SaveCheckpointsUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final List<Checkpoint> checkpoints;

    public SaveCheckpointsUseCase(@NonNull Scheduler executionThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager,
            @NonNull List<Checkpoint> checkpoints) {
        super(executionThread, postExecutionThread);
        this.storageManager = storageManager;
        this.checkpoints = checkpoints;
    }

    @Override
    public Single<long[]> perform() {
        return Single.fromCallable(() -> {
            for (Checkpoint checkpoint : checkpoints) {
                try {
                    String latitude = checkpoint.getLatitude();
                    String longitude = checkpoint.getLongitude();
                    checkpoint.setLatitude(latitude);
                    checkpoint.setLongitude(longitude);
                } catch (NumberFormatException e) {
                    checkpoints.remove(checkpoint);
                    e.printStackTrace();
                }
            }
            return storageManager.checkpointsDao()
                    .insert(checkpoints);
        }).subscribeOn(postExecutionThread)
                .observeOn(postExecutionThread);
    }
}

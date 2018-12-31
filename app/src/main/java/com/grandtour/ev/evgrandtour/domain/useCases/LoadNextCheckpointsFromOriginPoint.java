package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;
import com.grandtour.ev.evgrandtour.domain.models.NavigationPathData;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class LoadNextCheckpointsFromOriginPoint extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    private final int checkpointId;
    private final int maxCheckpointsToRetrieve;
    private final int startCheckpointId;
    private final int endCheckpointId;

    public LoadNextCheckpointsFromOriginPoint(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, int checkpointId, int maxCheckpointsToRetrieve, int startCheckpointId, int endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.checkpointId = checkpointId;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
        this.maxCheckpointsToRetrieve = maxCheckpointsToRetrieve;
    }

    @Override
    public Single<NavigationPathData> perform() {
        return storageManager.checkpointsDao()
                .getNextCheckpointsFromOrigin(checkpointId, maxCheckpointsToRetrieve, startCheckpointId, endCheckpointId)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<List<Checkpoint>, SingleSource<NavigationPathData>>() {
                    @Override
                    public SingleSource<NavigationPathData> apply(List<Checkpoint> checkpoints) throws Exception {
                        return Single.fromCallable(() -> {
                            List<Integer> associatedRouteLegIds = new ArrayList<>();
                            for (int index = 0; index < checkpoints.size() - 1; index++) {
                                int routeStartCheckpointId = checkpoints.get(index)
                                        .getCheckpointId();
                                int routeEndCheckpointId = checkpoints.get(index + 1)
                                        .getCheckpointId();

                                RouteLeg routeLeg = storageManager.routeLegDao()
                                        .getRouteLegForStartCheckpointAndEndCheckpoint(routeStartCheckpointId, routeEndCheckpointId);
                                if (routeLeg != null) {
                                    int routeLegId = routeLeg.getRouteLegId();
                                    associatedRouteLegIds.add(routeLegId);
                                }
                            }
                            return new NavigationPathData(checkpoints, associatedRouteLegIds);
                        });
                    }
                });
        //return null;
    }
}

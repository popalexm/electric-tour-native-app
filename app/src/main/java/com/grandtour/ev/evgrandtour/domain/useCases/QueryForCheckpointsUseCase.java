package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;

public class QueryForCheckpointsUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final List<MapCheckpoint> displayedMapCheckpoints;
    @NonNull
    private final String queryText;

    public QueryForCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull List<MapCheckpoint> currentlyDisplayedCheckpoints, @NonNull String queryText) {
        super(executorThread, postExecutionThread);
        this.displayedMapCheckpoints = currentlyDisplayedCheckpoints;
        this.queryText = queryText;
    }

    @Override
    public Maybe<List<MapCheckpoint>> perform() {
        return Maybe.fromCallable(() -> {
            List<MapCheckpoint> mapCheckpointsThatMatchQuery = new ArrayList<>();
            for (MapCheckpoint mapCheckpoint : displayedMapCheckpoints) {
                if (mapCheckpoint.getMapCheckpointTitle()
                        .contains(queryText)) {
                    mapCheckpointsThatMatchQuery.add(mapCheckpoint);
                    // Special case to filter checkpoints if user enters a number by their orderInTourId
                } else if (TextUtils.isDigitsOnly(queryText)) {
                    if (mapCheckpoint.getOrderInRouteId()
                            .equals(Integer.valueOf(queryText))) {
                        mapCheckpointsThatMatchQuery.add(mapCheckpoint);
                    }
                }
            }
            return mapCheckpointsThatMatchQuery;
        })
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

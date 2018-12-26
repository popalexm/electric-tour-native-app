package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadRouteLegsAndStepsForFilteredCheckpointsUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;
    private final int startCheckpointId;
    private final int endCheckpointId;

    public LoadRouteLegsAndStepsForFilteredCheckpointsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager, int startCheckpointId, int endCheckpointId) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
        this.startCheckpointId = startCheckpointId;
        this.endCheckpointId = endCheckpointId;
    }

    @Override
    public Maybe<List<Pair<RouteLeg, List<RouteStep>>>> perform() {
        return storageManager.routeLegDao()
                .getRouteLegsForStartAndEndCheckpoints(startCheckpointId, endCheckpointId)
                .flatMap((Function<List<RouteLeg>, MaybeSource<List<Pair<RouteLeg, List<RouteStep>>>>>) routeLegs -> Maybe.fromCallable(
                        new Callable<List<Pair<RouteLeg, List<RouteStep>>>>() {
                            @Override
                            public List<Pair<RouteLeg, List<RouteStep>>> call() {
                                List<Pair<RouteLeg, List<RouteStep>>> routeLegsAndSteps = new ArrayList<>();

                                for (RouteLeg routeLeg : routeLegs) {
                                    List<RouteStep> routeSteps = storageManager.routeStepDao()
                                            .getStepsForLegId(routeLeg.getRouteLegId());
                                    routeLegsAndSteps.add(new Pair<>(routeLeg, routeSteps));
                                }
                                return routeLegsAndSteps;
                            }
                        }));
    }
}

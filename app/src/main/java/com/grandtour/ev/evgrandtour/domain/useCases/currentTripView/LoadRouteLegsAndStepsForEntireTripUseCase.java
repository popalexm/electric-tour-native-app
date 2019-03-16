package com.grandtour.ev.evgrandtour.domain.useCases.currentTripView;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class LoadRouteLegsAndStepsForEntireTripUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private final LocalStorageManager storageManager;

    public LoadRouteLegsAndStepsForEntireTripUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<Pair<RouteLeg, List<RouteStep>>>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<String, MaybeSource<List<Pair<RouteLeg, List<RouteStep>>>>>() {
                    @Override
                    public MaybeSource<List<Pair<RouteLeg, List<RouteStep>>>> apply(String tourId) {
                        return Maybe.fromCallable(() -> {
                            List<Route> routes = storageManager.routeDao()
                                    .getRoutesForTourId(tourId);

                            List<Pair<RouteLeg, List<RouteStep>>> routeStepList = new ArrayList<>();

                            for (Route route : routes) {
                                int routeId = route.getRouteId();
                                List<RouteLeg> routeLegs = storageManager.routeLegDao()
                                        .getRouteLegsForTourId(routeId);

                                for (RouteLeg routeLeg : routeLegs) {
                                    int routeLegId = routeLeg.getRouteLegId();
                                    List<RouteStep> steps = storageManager.routeStepDao()
                                            .getStepsForLegId(routeLegId);
                                    routeStepList.add(new Pair<>(routeLeg, steps));
                                }
                            }
                            return routeStepList;
                        });
                    }
                });
    }
}

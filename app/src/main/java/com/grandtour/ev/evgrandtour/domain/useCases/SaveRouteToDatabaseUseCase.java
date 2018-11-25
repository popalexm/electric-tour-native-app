package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Leg;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RouteResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Step;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class SaveRouteToDatabaseUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final RouteResponse routeResponse;

    public SaveRouteToDatabaseUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull RouteResponse routeResponse) {
        super(executorThread, postExecutionThread);
        this.storageManager = localStorageManager;
        this.routeResponse = routeResponse;
    }

    @Override
    public Maybe<Long> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(executorThread)
                .observeOn(executorThread)
                .flatMap(new Function<String, MaybeSource<Long>>() {
                    @Override
                    public MaybeSource<Long> apply(String tourId) {
                        return Maybe.fromCallable(() -> {
                            Route route = new Route();
                            route.setTourId(tourId);
                            route.setRoutePolyline(routeResponse.getOverviewPolyline()
                                    .getPoints());
                            long routeId = storageManager.routeDao()
                                    .insert(route);

                            List<Leg> responseLegs = routeResponse.getLegs();
                            for (int i = 0; i < responseLegs.size(); i++) {
                                Leg legResponse = responseLegs.get(i);
                                RouteLeg routeLeg = SaveRouteToDatabaseUseCase.convertResponseToRouteLeg(legResponse, (int) routeId);
                                long routeLegId = storageManager.routeLegDao()
                                        .insertRouteLeg(routeLeg);

                                List<Step> responseSteps = legResponse.getSteps();
                                List<RouteStep> routeStepsList = new ArrayList<>();
                                for (Step stepResponse : responseSteps) {
                                    RouteStep routeStep = SaveRouteToDatabaseUseCase.convertResponseToRouteStep(stepResponse, (int) routeLegId);
                                    routeStepsList.add(routeStep);
                                }
                                storageManager.routeStepDao()
                                        .insertRouteLeg(routeStepsList);
                            }
                            return routeId;
                        });
                    }
                });
    }

    @NonNull
    private static RouteLeg convertResponseToRouteLeg(@NonNull Leg legResponse, int routeId) {
        RouteLeg routeLeg = new RouteLeg();
        routeLeg.setRouteId(routeId);
        routeLeg.setRouteLegStartLatitude(legResponse.getStartLocation()
                .getLat());
        routeLeg.setRouteLegStartLongitude(legResponse.getStartLocation()
                .getLng());
        routeLeg.setRouteLegEndLatitude(legResponse.getStartLocation()
                .getLat());
        routeLeg.setRouteLegEndLongitude(legResponse.getStartLocation()
                .getLng());
        return routeLeg;
    }

    @NonNull
    private static RouteStep convertResponseToRouteStep(@NonNull Step stepResponse, int routeLegId) {
        RouteStep routeStep = new RouteStep();
        routeStep.setRouteLegId(routeLegId);
        routeStep.setRouteStepStartLatitude(stepResponse.getStartLocation()
                .getLat());
        routeStep.setRouteStepStartLongitude(stepResponse.getStartLocation()
                .getLng());
        routeStep.setRouteStepEndLatitude(stepResponse.getEndLocation()
                .getLat());
        routeStep.setRouteStepEndLongitude(stepResponse.getEndLocation()
                .getLng());
        routeStep.setRouteStepPolyline(stepResponse.getPolyline()
                .getPoints());
        return routeStep;
    }
}

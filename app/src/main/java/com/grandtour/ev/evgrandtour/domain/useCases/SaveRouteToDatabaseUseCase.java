package com.grandtour.ev.evgrandtour.domain.useCases;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Route;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

public class SaveRouteToDatabaseUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final List<LatLng> routeMapPoints;

    public SaveRouteToDatabaseUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull List<LatLng> routeMapPoints) {
        super(executorThread, postExecutionThread);
        this.storageManager = localStorageManager;
        this.routeMapPoints = routeMapPoints;
    }

    @Override
    public Single<Long[]> perform() {
        Single<Long> insertRoute = Single.fromCallable(() -> {
            Route route = new Route();
            return storageManager.routeDao()
                    .insert(route);
        }).subscribeOn(executorThread).observeOn(executorThread);

        return insertRoute.flatMap((Function<Long, SingleSource<Long[]>>) routeId -> {
            List<RouteWaypoint> routeWaypointList = new ArrayList<>();
            for (LatLng waypoint : routeMapPoints) {
                RouteWaypoint routeWaypoint = new RouteWaypoint();
                routeWaypoint.setLat(waypoint.latitude);
                routeWaypoint.setLng(waypoint.longitude);
                routeWaypoint.setRouteId(routeId.intValue());
                routeWaypointList.add(routeWaypoint);
            }
            long[] waypoints = storageManager.routeWaypointsDao()
                    .insert(routeWaypointList);
            return Single.just(ArrayUtils.toWrapperArray(waypoints));
        })
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }
}

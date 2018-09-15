package com.grandtour.ev.evgrandtour.domain;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Route;
import com.grandtour.ev.evgrandtour.data.persistence.models.RouteWaypoint;
import com.grandtour.ev.evgrandtour.data.persistence.models.Waypoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SaveRouteToDatabaseUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private LocalStorageManager storageManager;
    @NonNull
    private  List<LatLng> routeMapPoints;

    public SaveRouteToDatabaseUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager localStorageManager, @NonNull List<LatLng> routeMapPoints) {
        super(executorThread, postExecutionThread);
        this.storageManager = localStorageManager;
        this.routeMapPoints = routeMapPoints;
    }

    @Override
    public Single<Long[]> perform() {
        Single<Long> insertRoute = Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                Log.e("TAG", "Inserting new route");
                Route route = new Route();
                return storageManager.routeDao().insertNewRoute(route);
            }
        }).subscribeOn(executorThread).observeOn(executorThread);
       return insertRoute.flatMap(new Function<Long, SingleSource<Long[]>>() {
            @Override
            public SingleSource<Long[]> apply(Long routeId) throws Exception {
                List<RouteWaypoint> routeWaypoints = new ArrayList<>();
                for (LatLng waypoint : routeMapPoints) {
                    RouteWaypoint routeWaypoint = new RouteWaypoint();
                    routeWaypoint.setLat(waypoint.latitude);
                    routeWaypoint.setLng(waypoint.longitude);
                    routeWaypoint.setRouteId(routeId.intValue());
                }
                long[] waypoints = storageManager.routeWaypointsDao().addWaypoints(routeWaypoints);
                Log.e("TAG", "Inserting waypoints" + waypoints);
                return Single.just(ArrayUtils.toWrapperArray(waypoints));
            }
        }).subscribeOn(executorThread).observeOn(postExecutionThread);
    }
}

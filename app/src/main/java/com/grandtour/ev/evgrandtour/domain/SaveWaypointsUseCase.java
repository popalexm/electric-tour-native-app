package com.grandtour.ev.evgrandtour.domain;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.persistence.models.Waypoint;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseSingle;
import com.grandtour.ev.evgrandtour.utils.JSONUtils;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.Single;


public class SaveWaypointsUseCase extends BaseUseCase implements BaseUseCaseSingle {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final List<Waypoint> waypoints;

    public SaveWaypointsUseCase(@NonNull Scheduler executionThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager, @NonNull List<Waypoint> waypoints) {
        super(executionThread, postExecutionThread);
        this.storageManager = storageManager;
        this.waypoints = waypoints;
    }

    @Override
    public Single<long[]> perform() {
        return Single.fromCallable(() -> {
            for (Waypoint waypoint : waypoints) {
                try {
                    String latitude =  JSONUtils.filterLatLngValues(waypoint.getLatitude());
                    String longitude = JSONUtils.filterLatLngValues(waypoint.getLongitude());
                    waypoint.setLatitude(latitude);
                    waypoint.setLongitude(longitude);
                } catch (NumberFormatException e) {
                    waypoints.remove(waypoint);
                    e.printStackTrace();
                }
            }
            return storageManager.waypointsDao()
                    .insert(waypoints);
        }).subscribeOn(postExecutionThread)
                .observeOn(postExecutionThread);
    }
}

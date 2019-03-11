package com.grandtour.ev.evgrandtour.domain.models;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;

import java.util.List;

import androidx.annotation.NonNull;

public class NavigationPathData {

    @NonNull
    private final List<Checkpoint> navigationPathWayPoints;
    @NonNull
    private final List<Integer> navigationPathRouteLegs;

    public NavigationPathData(@NonNull List<Checkpoint> navigationPathWayPoints, @NonNull List<Integer> navigationPathRouteLegs) {
        this.navigationPathWayPoints = navigationPathWayPoints;
        this.navigationPathRouteLegs = navigationPathRouteLegs;
    }

    @NonNull
    public List<Checkpoint> getNavigationPathWayPoints() {
        return navigationPathWayPoints;
    }

    @NonNull
    public List<Integer> getNavigationPathRouteLegs() {
        return navigationPathRouteLegs;
    }
}

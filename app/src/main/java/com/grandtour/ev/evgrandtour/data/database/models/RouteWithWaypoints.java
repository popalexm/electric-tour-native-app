package com.grandtour.ev.evgrandtour.data.database.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class RouteWithWaypoints {

    @Embedded
    public Route route;
    @Relation(parentColumn = "routeId", entityColumn = "routeId", entity = RouteWaypoint.class)
    public List<RouteWaypoint> routeWaypoints;

}

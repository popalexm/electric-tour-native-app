package com.grandtour.ev.evgrandtour.data.persistence.models;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int routeId;
    // Route distance in meters
    private int distance;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}

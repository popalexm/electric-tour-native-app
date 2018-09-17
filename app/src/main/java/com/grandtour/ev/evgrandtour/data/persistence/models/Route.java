package com.grandtour.ev.evgrandtour.data.persistence.models;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int routeId;

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
}

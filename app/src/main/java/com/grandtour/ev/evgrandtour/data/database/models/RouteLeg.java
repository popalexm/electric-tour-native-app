package com.grandtour.ev.evgrandtour.data.database.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Route.class, parentColumns = "routeId", childColumns = "routeId", onDelete = CASCADE))
public class RouteLeg {

    @PrimaryKey(autoGenerate = true)
    private int routeLegId;
    private int routeId;
    private int startCheckpointId;
    private int endCheckpointId;
    private double routeLegStartLatitude;
    private double routeLegStartLongitude;
    private double routeLegEndLatitude;
    private double routeLegEndLongitude;

    public int getRouteLegId() {
        return routeLegId;
    }

    public void setRouteLegId(int routeLegId) {
        this.routeLegId = routeLegId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public double getRouteLegStartLatitude() {
        return routeLegStartLatitude;
    }

    public void setRouteLegStartLatitude(double routeLegStartLatitude) {
        this.routeLegStartLatitude = routeLegStartLatitude;
    }

    public double getRouteLegStartLongitude() {
        return routeLegStartLongitude;
    }

    public void setRouteLegStartLongitude(double routeLegStartLongitude) {
        this.routeLegStartLongitude = routeLegStartLongitude;
    }

    public double getRouteLegEndLatitude() {
        return routeLegEndLatitude;
    }

    public void setRouteLegEndLatitude(double routeLegEndLatitude) {
        this.routeLegEndLatitude = routeLegEndLatitude;
    }

    public double getRouteLegEndLongitude() {
        return routeLegEndLongitude;
    }

    public void setRouteLegEndLongitude(double routeLegEndLongitude) {
        this.routeLegEndLongitude = routeLegEndLongitude;
    }

    public int getStartCheckpointId() {
        return startCheckpointId;
    }

    public void setStartCheckpointId(int startCheckpointId) {
        this.startCheckpointId = startCheckpointId;
    }

    public int getEndCheckpointId() {
        return endCheckpointId;
    }

    public void setEndCheckpointId(int endCheckpointId) {
        this.endCheckpointId = endCheckpointId;
    }
}

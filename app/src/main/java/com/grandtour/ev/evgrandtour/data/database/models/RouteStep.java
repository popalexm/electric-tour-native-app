package com.grandtour.ev.evgrandtour.data.database.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = RouteLeg.class, parentColumns = "routeLegId", childColumns = "routeLegId", onDelete = CASCADE))
public class RouteStep {

    @PrimaryKey(autoGenerate = true)
    private int routeStepId;
    private int routeLegId;
    private double routeStepStartLatitude;
    private double routeStepStartLongitude;
    private double routeStepEndLatitude;
    private double routeStepEndLongitude;
    private String routeStepPolyline;

    public int getRouteStepId() {
        return routeStepId;
    }

    public void setRouteStepId(int routeStepId) {
        this.routeStepId = routeStepId;
    }

    public int getRouteLegId() {
        return routeLegId;
    }

    public void setRouteLegId(int routeLegId) {
        this.routeLegId = routeLegId;
    }

    public String getRouteStepPolyline() {
        return routeStepPolyline;
    }

    public void setRouteStepPolyline(String routeStepPolyline) {
        this.routeStepPolyline = routeStepPolyline;
    }

    public double getRouteStepStartLatitude() {
        return routeStepStartLatitude;
    }

    public void setRouteStepStartLatitude(double routeStepStartLatitude) {
        this.routeStepStartLatitude = routeStepStartLatitude;
    }

    public double getRouteStepStartLongitude() {
        return routeStepStartLongitude;
    }

    public void setRouteStepStartLongitude(double routeStepStartLongitude) {
        this.routeStepStartLongitude = routeStepStartLongitude;
    }

    public double getRouteStepEndLatitude() {
        return routeStepEndLatitude;
    }

    public void setRouteStepEndLatitude(double routeStepEndLatitude) {
        this.routeStepEndLatitude = routeStepEndLatitude;
    }

    public double getRouteStepEndLongitude() {
        return routeStepEndLongitude;
    }

    public void setRouteStepEndLongitude(double routeStepEndLongitude) {
        this.routeStepEndLongitude = routeStepEndLongitude;
    }
}

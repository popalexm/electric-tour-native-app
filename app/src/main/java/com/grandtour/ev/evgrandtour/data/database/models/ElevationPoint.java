package com.grandtour.ev.evgrandtour.data.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = RouteLeg.class, parentColumns = "routeLegId", childColumns = "routeLegId", onDelete = CASCADE))
public class ElevationPoint {

    @PrimaryKey(autoGenerate = true)
    private int elevationPointId;
    private int routeLegId;
    private int startCheckpointOrderId;
    private double latitude;
    private double longitude;
    private double elevation;

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public int getElevationPointId() {
        return elevationPointId;
    }

    public void setElevationPointId(int elevationPointId) {
        this.elevationPointId = elevationPointId;
    }

    public int getRouteLegId() {
        return routeLegId;
    }

    public void setRouteLegId(int routeLegId) {
        this.routeLegId = routeLegId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getStartCheckpointOrderId() {
        return startCheckpointOrderId;
    }

    public void setStartCheckpointOrderId(int startCheckpointOrderId) {
        this.startCheckpointOrderId = startCheckpointOrderId;
    }

}

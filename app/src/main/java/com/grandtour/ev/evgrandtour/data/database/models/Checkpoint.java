package com.grandtour.ev.evgrandtour.data.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Checkpoint {

    @PrimaryKey
    private Integer checkpointId;
    private String checkpointName;
    private double latitude;
    private double longitude;
    private Integer distanceToNextCheckpoint;

    public Integer getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(Integer checkpointId) {
        this.checkpointId = checkpointId;
    }

    public String getCheckpointName() {
        return checkpointName;
    }

    public void setCheckpointName(String checkpointName) {
        this.checkpointName = checkpointName;
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

    public Integer getDistanceToNextCheckpoint() {
        return distanceToNextCheckpoint;
    }

    public void setDistanceToNextCheckpoint(Integer distanceToNextCheckpoint) {
        this.distanceToNextCheckpoint = distanceToNextCheckpoint;
    }
}

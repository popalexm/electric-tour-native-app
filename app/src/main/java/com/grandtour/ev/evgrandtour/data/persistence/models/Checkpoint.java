
package com.grandtour.ev.evgrandtour.data.persistence.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Checkpoint {

    @PrimaryKey
    private Integer checkpointId;
    private String checkpointName;
    private String latitude;
    private String longitude;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getDistanceToNextCheckpoint() {
        return distanceToNextCheckpoint;
    }

    public void setDistanceToNextCheckpoint(Integer distanceToNextCheckpoint) {
        this.distanceToNextCheckpoint = distanceToNextCheckpoint;
    }
}

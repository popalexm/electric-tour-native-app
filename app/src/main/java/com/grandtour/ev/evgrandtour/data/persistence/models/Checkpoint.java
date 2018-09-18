
package com.grandtour.ev.evgrandtour.data.persistence.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Checkpoint {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    private Integer checkpointId;
    @SerializedName("NAME")
    @Expose
    private String checkpointName;
    @SerializedName("LAT")
    @Expose
    private String latitude;
    @SerializedName("LONG")
    @Expose
    private String longitude;
    @SerializedName("DISTANCE TO THE NEXT LOCALITY (meters)")
    @Expose
    private Integer distanceToNext;

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

    public Integer getDistanceToNext() {
        return distanceToNext;
    }

    public void setDistanceToNext(Integer distanceToNext) {
        this.distanceToNext = distanceToNext;
    }
}

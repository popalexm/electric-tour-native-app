
package com.grandtour.ev.evgrandtour.data.persistence.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.grandtour.ev.evgrandtour.data.persistence.LocalStorageManager;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = LocalStorageManager.WAYPOINTS_TABLE_NAME)
public class Waypoint {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    private Integer waypointId;
    @SerializedName("NAME")
    @Expose
    private String waypointName;
    @SerializedName("LAT")
    @Expose
    private String latitude;
    @SerializedName("LONG")
    @Expose
    private String longitude;
    @SerializedName("DISTANCE TO THE NEXT LOCALITY (meters)")
    @Expose
    private Integer distanceToNext;

    public Integer getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(Integer waypointId) {
        this.waypointId = waypointId;
    }

    public String getWaypointName() {
        return waypointName;
    }

    public void setWaypointName(String waypointName) {
        this.waypointName = waypointName;
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

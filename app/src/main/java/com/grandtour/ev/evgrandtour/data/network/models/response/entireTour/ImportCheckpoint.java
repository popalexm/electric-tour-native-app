package com.grandtour.ev.evgrandtour.data.network.models.response.entireTour;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImportCheckpoint {

    @SerializedName("ID")
    @Expose
    private Integer checkpointId;
    @SerializedName("Description")
    @Expose
    private String checkpointName;
    @SerializedName("Latitude")
    @Expose
    private String latitude;
    @SerializedName("Longitude")
    @Expose
    private String longitude;

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

}

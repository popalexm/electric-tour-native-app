package com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TourCheckpoint {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("ID")
    @Expose
    private Integer tourOrderId;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTourOrderId() {
        return tourOrderId;
    }

    public void setID(Integer iD) {
        this.tourOrderId = iD;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}

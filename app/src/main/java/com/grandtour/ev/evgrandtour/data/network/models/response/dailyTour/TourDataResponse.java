package com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.NonNull;

public class TourDataResponse {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("isEntireTour")
    @Expose
    private Boolean isEntireTour;
    @SerializedName("points")
    @Expose
    private List<TourCheckpoint> tourCheckpoints;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsEntireTour() {
        return isEntireTour;
    }

    public void setIsEntireTour(Boolean isEntireTour) {
        this.isEntireTour = isEntireTour;
    }

    public List<TourCheckpoint> getTourCheckpoints() {
        return tourCheckpoints;
    }

    public void setTourCheckpoints(List<TourCheckpoint> tourCheckpoints) {
        this.tourCheckpoints = tourCheckpoints;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    @NonNull
    @Override
    public String toString(){
        return name;
    }

}

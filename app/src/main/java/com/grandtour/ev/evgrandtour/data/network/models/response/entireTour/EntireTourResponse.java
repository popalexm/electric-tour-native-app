
package com.grandtour.ev.evgrandtour.data.network.models.response.entireTour;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntireTourResponse {

    @SerializedName("points")
    @Expose
    private List<ImportCheckpoint> points = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isEntireTour")
    @Expose
    private Boolean isEntireTour;

    public List<ImportCheckpoint> getPoints() {
        return points;
    }

    public void setPoints(List<ImportCheckpoint> points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsEntireTour() {
        return isEntireTour;
    }

    public void setIsEntireTour(Boolean isEntireTour) {
        this.isEntireTour = isEntireTour;
    }

}

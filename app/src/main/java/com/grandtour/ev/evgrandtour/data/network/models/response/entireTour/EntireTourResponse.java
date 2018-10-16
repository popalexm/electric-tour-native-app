
package com.grandtour.ev.evgrandtour.data.network.models.response.entireTour;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EntireTourResponse {

    @SerializedName("points")
    @Expose
    private List<Point> points = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isEntireTour")
    @Expose
    private Boolean isEntireTour;

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
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

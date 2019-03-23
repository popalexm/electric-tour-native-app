package com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip;

import java.util.List;

public class InPlanningTripResponse {

    private Integer tripId;
    private Integer userId;
    private String tripName;
    private String tripDescription;
    private List<InPlanningCheckpointResponse> inPlanningCheckpoints;

    public String getTripName() {
        return tripName;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public List<InPlanningCheckpointResponse> getInPlanningCheckpoints() {
        return inPlanningCheckpoints;
    }

    public Integer getTripId() {
        return tripId;
    }

}

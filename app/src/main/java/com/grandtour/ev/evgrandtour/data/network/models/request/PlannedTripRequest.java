package com.grandtour.ev.evgrandtour.data.network.models.request;

import java.util.List;

public class PlannedTripRequest {

    private Integer tripId;
    private String tripName;
    private String tripDescription;
    private List<PlannedCheckpointRequest> plannedCheckpointRequests;

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripDescription() {
        return tripDescription;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public List<PlannedCheckpointRequest> getPlannedCheckpointRequests() {
        return plannedCheckpointRequests;
    }

    public void setPlannedCheckpointRequests(List<PlannedCheckpointRequest> plannedCheckpointRequests) {
        this.plannedCheckpointRequests = plannedCheckpointRequests;
    }
}

package com.grandtour.ev.evgrandtour.data.network.models.request;


public class AddInPlanningTripRequest {

    private String tripName;
    private String tripDescription;
    private Integer userId;

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public void setTripDescription(String tripDescription) {
        this.tripDescription = tripDescription;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

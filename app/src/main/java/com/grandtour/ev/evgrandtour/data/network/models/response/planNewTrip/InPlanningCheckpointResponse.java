package com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip;

public class InPlanningCheckpointResponse {

    private Integer checkpointId;
    private Integer orderInTripId;
    private Integer tripId;
    private double checkpointLatitude;
    private double checkpointLongitude;
    private String checkpointTitle;
    private String checkpointDescription;
    private String checkpointAddress;
    private boolean areArrivalNotificationsEnabled;
    private boolean areDepartureNotificationsEnabled;
    private int checkpointColor;

    public Integer getCheckpointId() {
        return checkpointId;
    }

    public Integer getOrderInTripId() {
        return orderInTripId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public double getCheckpointLatitude() {
        return checkpointLatitude;
    }

    public double getCheckpointLongitude() {
        return checkpointLongitude;
    }

    public String getCheckpointTitle() {
        return checkpointTitle;
    }

    public String getCheckpointDescription() {
        return checkpointDescription;
    }

    public String getCheckpointAddress() {
        return checkpointAddress;
    }

    public boolean areArrivalNotificationsEnabled() {
        return areArrivalNotificationsEnabled;
    }

    public boolean areDepartureNotificationsEnabled() {
        return areDepartureNotificationsEnabled;
    }

    public int getCheckpointColor() {
        return checkpointColor;
    }

}

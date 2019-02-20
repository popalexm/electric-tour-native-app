package com.grandtour.ev.evgrandtour.data.database.models;

import com.grandtour.ev.evgrandtour.data.database.DatabaseColumnsConstants;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = InPlanningTrip.class, parentColumns = DatabaseColumnsConstants.TRIP_ID, childColumns = DatabaseColumnsConstants.TRIP_ID, onDelete = CASCADE))
public class InPlanningCheckpoint {

    @PrimaryKey(autoGenerate = true)
    private Integer checkpointId;
    private Integer orderInTripId;
    // Used for one-to-many relationships with the parent trip table
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

    public void setCheckpointId(Integer checkpointId) {
        this.checkpointId = checkpointId;
    }

    public String getCheckpointTitle() {
        return checkpointTitle;
    }

    public void setCheckpointTitle(String checkpointTitle) {
        this.checkpointTitle = checkpointTitle;
    }

    public String getCheckpointDescription() {
        return checkpointDescription;
    }

    public void setCheckpointDescription(String checkpointDescription) {
        this.checkpointDescription = checkpointDescription;
    }

    public boolean isAreArrivalNotificationsEnabled() {
        return areArrivalNotificationsEnabled;
    }

    public void setAreArrivalNotificationsEnabled(boolean areArrivalNotificationsEnabled) {
        this.areArrivalNotificationsEnabled = areArrivalNotificationsEnabled;
    }

    public boolean isAreDepartureNotificationsEnabled() {
        return areDepartureNotificationsEnabled;
    }

    public void setAreDepartureNotificationsEnabled(boolean areDepartureNotificationsEnabled) {
        this.areDepartureNotificationsEnabled = areDepartureNotificationsEnabled;
    }

    public int getCheckpointColor() {
        return checkpointColor;
    }

    public void setCheckpointColor(int checkpointColor) {
        this.checkpointColor = checkpointColor;
    }

    public String getCheckpointAddress() {
        return checkpointAddress;
    }

    public void setCheckpointAddress(String checkpointAddress) {
        this.checkpointAddress = checkpointAddress;
    }

    public Integer getOrderInTripId() {
        return orderInTripId;
    }

    public void setOrderInTripId(Integer orderInTripId) {
        this.orderInTripId = orderInTripId;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public double getCheckpointLatitude() {
        return checkpointLatitude;
    }

    public void setCheckpointLatitude(double checkpointLatitude) {
        this.checkpointLatitude = checkpointLatitude;
    }

    public double getCheckpointLongitude() {
        return checkpointLongitude;
    }

    public void setCheckpointLongitude(double checkpointLongitude) {
        this.checkpointLongitude = checkpointLongitude;
    }
}

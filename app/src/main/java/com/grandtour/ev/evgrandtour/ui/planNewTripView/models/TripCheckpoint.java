package com.grandtour.ev.evgrandtour.ui.planNewTripView.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import android.os.Parcel;
import android.os.Parcelable;

public final class TripCheckpoint implements ClusterItem, Parcelable {

    private Integer checkpointId;
    private Integer orderInTrip;
    private LatLng geographicalPosition;
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

    protected TripCheckpoint(Parcel in) {
        this.checkpointId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.orderInTrip = (Integer) in.readValue(Integer.class.getClassLoader());
        this.geographicalPosition = in.readParcelable(LatLng.class.getClassLoader());
        this.checkpointTitle = in.readString();
        this.checkpointDescription = in.readString();
        this.checkpointAddress = in.readString();
        this.areArrivalNotificationsEnabled = in.readByte() != 0;
        this.areDepartureNotificationsEnabled = in.readByte() != 0;
        this.checkpointColor = in.readInt();
    }

    public Integer getOrderInTrip() {
        return orderInTrip;
    }

    public LatLng getGeographicalPosition() {
        return geographicalPosition;
    }

    public void setGeographicalPosition(LatLng geographicalPosition) {
        this.geographicalPosition = geographicalPosition;
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

    public boolean areArrivalNotificationsEnabled() {
        return areArrivalNotificationsEnabled;
    }

    public void setAreArrivalNotificationsEnabled(boolean areArrivalNotificationsEnabled) {
        this.areArrivalNotificationsEnabled = areArrivalNotificationsEnabled;
    }

    public boolean areDepartureNotificationsEnabled() {
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

    @Override
    public LatLng getPosition() {
        return geographicalPosition;
    }

    @Override
    public String getTitle() {
        return checkpointTitle;
    }

    @Override
    public String getSnippet() {
        return checkpointDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public TripCheckpoint() {
    }

    public void setOrderInTrip(Integer orderInTrip) {
        this.orderInTrip = orderInTrip;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.checkpointId);
        dest.writeValue(this.orderInTrip);
        dest.writeParcelable(this.geographicalPosition, flags);
        dest.writeString(this.checkpointTitle);
        dest.writeString(this.checkpointDescription);
        dest.writeString(this.checkpointAddress);
        dest.writeByte(this.areArrivalNotificationsEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.areDepartureNotificationsEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.checkpointColor);
    }

    public static final Parcelable.Creator<TripCheckpoint> CREATOR = new Parcelable.Creator<TripCheckpoint>() {
        @Override
        public TripCheckpoint createFromParcel(Parcel source) {
            return new TripCheckpoint(source);
        }

        @Override
        public TripCheckpoint[] newArray(int size) {
            return new TripCheckpoint[size];
        }
    };
}

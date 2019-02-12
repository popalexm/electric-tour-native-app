package com.grandtour.ev.evgrandtour.ui.planNewTripView.models;

import com.google.android.gms.maps.model.LatLng;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public final class TripCheckpoint implements Parcelable {

    private Integer checkpointId;
    private LatLng geographicalPosition;
    private String checkpointTitle;
    private String checkpointDescription;
    private boolean areArrivalNotificationsEnabled;
    private boolean areDepartureNotificationsEnabled;

    private TripCheckpoint(@NonNull Integer checkpointId, @NonNull LatLng geographicalPosition, @NonNull String checkpointTitle,
            @NonNull String checkpointDescription, boolean areArrivalNotificationsEnabled, boolean areDepartureNotificationsEnabled) {
        this.checkpointId = checkpointId;
        this.geographicalPosition = geographicalPosition;
        this.checkpointTitle = checkpointTitle;
        this.checkpointDescription = checkpointDescription;
        this.areArrivalNotificationsEnabled = areArrivalNotificationsEnabled;
        this.areDepartureNotificationsEnabled = areDepartureNotificationsEnabled;
    }

    private TripCheckpoint(Parcel in) {
        this.checkpointId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.geographicalPosition = in.readParcelable(LatLng.class.getClassLoader());
        this.checkpointTitle = in.readString();
        this.checkpointDescription = in.readString();
        this.areArrivalNotificationsEnabled = in.readByte() != 0;
        this.areDepartureNotificationsEnabled = in.readByte() != 0;
    }

    public Integer getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(Integer checkpointId) {
        this.checkpointId = checkpointId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.checkpointId);
        dest.writeParcelable(this.geographicalPosition, flags);
        dest.writeString(this.checkpointTitle);
        dest.writeString(this.checkpointDescription);
        dest.writeByte(this.areArrivalNotificationsEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.areDepartureNotificationsEnabled ? (byte) 1 : (byte) 0);
    }

    public static class TripCheckpointBuilder {

        private Integer checkpointId;
        private LatLng geographicalPosition;
        private String checkpointTitle;
        private String checkpointDescription;
        private boolean areArrivalNotificationsEnabled;
        private boolean areDepartureNotificationsEnabled;

        public TripCheckpointBuilder setCheckpointId(Integer checkpointId) {
            this.checkpointId = checkpointId;
            return this;
        }

        public TripCheckpointBuilder setGeographicalPosition(LatLng geographicalPosition) {
            this.geographicalPosition = geographicalPosition;
            return this;
        }

        public TripCheckpointBuilder setCheckpointTitle(String checkpointTitle) {
            this.checkpointTitle = checkpointTitle;
            return this;
        }

        public TripCheckpointBuilder setCheckpointDescription(String checkpointDescription) {
            this.checkpointDescription = checkpointDescription;
            return this;
        }

        public TripCheckpointBuilder setAreArrivalNotificationsEnabled(boolean areArrivalNotificationsEnabled) {
            this.areArrivalNotificationsEnabled = areArrivalNotificationsEnabled;
            return this;
        }

        public TripCheckpointBuilder setAreDepartureNotificationsEnabled(boolean areDepartureNotificationsEnabled) {
            this.areDepartureNotificationsEnabled = areDepartureNotificationsEnabled;
            return this;
        }

        public TripCheckpoint createTripCheckpoint() {
            return new TripCheckpoint(checkpointId, geographicalPosition, checkpointTitle, checkpointDescription, areArrivalNotificationsEnabled,
                    areDepartureNotificationsEnabled);
        }
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

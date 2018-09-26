package com.grandtour.ev.evgrandtour.data.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Checkpoint implements Parcelable {

    @PrimaryKey
    private Integer checkpointId;
    private String checkpointName;
    private double latitude;
    private double longitude;
    private Integer distanceToNextCheckpoint;

    public Integer getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(Integer checkpointId) {
        this.checkpointId = checkpointId;
    }

    public String getCheckpointName() {
        return checkpointName;
    }

    public void setCheckpointName(String checkpointName) {
        this.checkpointName = checkpointName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getDistanceToNextCheckpoint() {
        return distanceToNextCheckpoint;
    }

    public void setDistanceToNextCheckpoint(Integer distanceToNextCheckpoint) {
        this.distanceToNextCheckpoint = distanceToNextCheckpoint;
    }

    public Checkpoint() {
    }

    protected Checkpoint(Parcel in) {
        this.checkpointId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.checkpointName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distanceToNextCheckpoint = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.checkpointId);
        dest.writeString(this.checkpointName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeValue(this.distanceToNextCheckpoint);
    }

    public static final Parcelable.Creator<Checkpoint> CREATOR = new Parcelable.Creator<Checkpoint>() {
        @Override
        public Checkpoint createFromParcel(Parcel source) {
            return new Checkpoint(source);
        }

        @Override
        public Checkpoint[] newArray(int size) {
            return new Checkpoint[size];
        }
    };
}
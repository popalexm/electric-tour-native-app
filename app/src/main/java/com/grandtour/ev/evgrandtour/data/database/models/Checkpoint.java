package com.grandtour.ev.evgrandtour.data.database.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Tour.class, parentColumns = "tourId", childColumns = "tourId", onDelete = CASCADE))
public class Checkpoint implements Parcelable {

    @PrimaryKey
    private Integer checkpointId;
    private String tourId;
    private Integer orderInTourId;
    private String checkpointName;
    private double latitude;
    private double longitude;
    private Integer distanceToNextCheckpoint;
    private Integer durationToNextCheckpoint;

    public Checkpoint() {
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

    public void setDurationToNextCheckpoint(Integer durationToNextCheckpoint) {
        this.durationToNextCheckpoint = durationToNextCheckpoint;
    }

    public void setDistanceToNextCheckpoint(Integer distanceToNextCheckpoint) {
        this.distanceToNextCheckpoint = distanceToNextCheckpoint;
    }

    public Integer getCheckpointId() {
        return checkpointId;
    }

    protected Checkpoint(Parcel in) {
        this.checkpointId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tourId = in.readString();
        this.orderInTourId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.checkpointName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distanceToNextCheckpoint = (Integer) in.readValue(Integer.class.getClassLoader());
        this.durationToNextCheckpoint = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public Integer getDurationToNextCheckpoint() {
        return durationToNextCheckpoint;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public Integer getOrderInTourId() {
        return orderInTourId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setOrderInTourId(Integer orderInTourId) {
        this.orderInTourId = orderInTourId;
    }

    @NonNull
    @Override
    public String toString() {
        return checkpointName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.checkpointId);
        dest.writeString(this.tourId);
        dest.writeValue(this.orderInTourId);
        dest.writeString(this.checkpointName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeValue(this.distanceToNextCheckpoint);
        dest.writeValue(this.durationToNextCheckpoint);
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

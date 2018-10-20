package com.grandtour.ev.evgrandtour.data.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity()
public class Tour {

    @PrimaryKey
    @NonNull
    private String tourId;
    private String name;
    private boolean isEntireTour;
    private boolean isCurrentSelection;

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEntireTour() {
        return isEntireTour;
    }

    public void setEntireTour(boolean entireTour) {
        isEntireTour = entireTour;
    }

    public boolean isCurrentSelection() {
        return isCurrentSelection;
    }

    public void setCurrentSelection(boolean currentSelection) {
        isCurrentSelection = currentSelection;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class InPlanningTripDetails {

    @NonNull
    private final Integer inPlanningTripId;
    @Nullable
    private String inPlanningTripName;
    @Nullable
    private String inPlanningTripDescription;
    @Nullable
    private List<TripCheckpoint> plannedTripCheckpoints;

    public InPlanningTripDetails(@NonNull Integer inPlanningTripId) {
        this.inPlanningTripId = inPlanningTripId;
    }

    @Nullable
    public String getInPlanningTripName() {
        return inPlanningTripName;
    }

    public void setInPlanningTripName(@Nullable String inPlanningTripName) {
        this.inPlanningTripName = inPlanningTripName;
    }

    @Nullable
    public String getInPlanningTripDescription() {
        return inPlanningTripDescription;
    }

    public void setInPlanningTripDescription(@Nullable String inPlanningTripDescription) {
        this.inPlanningTripDescription = inPlanningTripDescription;
    }

    @Nullable
    public List<TripCheckpoint> getPlannedTripCheckpoints() {
        return plannedTripCheckpoints;
    }

    public void setPlannedTripCheckpoints(@Nullable List<TripCheckpoint> plannedTripCheckpoints) {
        this.plannedTripCheckpoints = plannedTripCheckpoints;
    }

    @NonNull
    public Integer getInPlanningTripId() {
        return inPlanningTripId;
    }
}

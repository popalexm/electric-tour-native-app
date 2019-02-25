package com.grandtour.ev.evgrandtour.ui.planNewTripView.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class InPlanningTripDetails {

    @NonNull
    private final Integer inPlanningTripId;
    @NonNull
    private String inPlanningTripName;
    @NonNull
    private String inPlanningTripDescription;
    @NonNull
    private List<TripCheckpoint> plannedTripCheckpoints;

    public InPlanningTripDetails(@NonNull Integer inPlanningTripId) {
        this.inPlanningTripId = inPlanningTripId;
        this.inPlanningTripName = "";
        this.inPlanningTripDescription = "";
        this.plannedTripCheckpoints = new ArrayList<>();
    }

    @NonNull
    public String getInPlanningTripName() {
        return inPlanningTripName;
    }

    public void setInPlanningTripName(@NonNull String inPlanningTripName) {
        this.inPlanningTripName = inPlanningTripName;
    }

    @NonNull
    public String getInPlanningTripDescription() {
        return inPlanningTripDescription;
    }

    public void setInPlanningTripDescription(@NonNull String inPlanningTripDescription) {
        this.inPlanningTripDescription = inPlanningTripDescription;
    }

    @NonNull
    public List<TripCheckpoint> getPlannedTripCheckpoints() {
        return plannedTripCheckpoints;
    }

    public void setPlannedTripCheckpoints(@NonNull List<TripCheckpoint> plannedTripCheckpoints) {
        this.plannedTripCheckpoints = plannedTripCheckpoints;
    }

    @NonNull
    public Integer getInPlanningTripId() {
        return inPlanningTripId;
    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class PlanNewTripViewModel {

    @NonNull
    public final ObservableBoolean isCheckpointNameIncomplete = new ObservableBoolean();
    @NonNull
    public ObservableField<String> tripTitle = new ObservableField<>("");

}

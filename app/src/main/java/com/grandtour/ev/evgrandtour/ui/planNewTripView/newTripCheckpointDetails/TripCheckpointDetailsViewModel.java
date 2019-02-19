package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class TripCheckpointDetailsViewModel {

    @NonNull
    public final ObservableBoolean isCheckpointNameIncomplete = new ObservableBoolean();
    @NonNull
    public final ObservableField<String> checkpointName = new ObservableField<>("");
    @NonNull
    public final ObservableField<String> checkpointDescription = new ObservableField<>("");
    @NonNull
    public final ObservableField<String> checkpointAddress = new ObservableField<>();
    @NonNull
    public final ObservableBoolean isCheckpointInEditMode = new ObservableBoolean();

}

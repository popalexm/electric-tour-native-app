package com.grandtour.ev.evgrandtour.ui.mapsView.distancePickerDialog;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

public class DistancePickerViewModel {

    @NonNull
    public final ObservableArrayList<Checkpoint> totalCheckpoints = new ObservableArrayList<>();
    @NonNull
    public final ObservableInt selectedStartCheckpoint = new ObservableInt();
    @NonNull
    public final ObservableInt selectedEndCheckpoint = new ObservableInt();
    @NonNull
    public final ObservableField<String> calculatedDistance = new ObservableField<>("");

}

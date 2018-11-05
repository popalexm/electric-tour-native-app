package com.grandtour.ev.evgrandtour.ui.mapsView.distancePickerDialog;

import android.support.annotation.NonNull;

public class DistancePointViewModel {

    @NonNull
    public Integer checkpointId;
    @NonNull
    public String checkpointName;

    DistancePointViewModel(@NonNull Integer checkpointId, @NonNull String checkpointName) {
        this.checkpointId = checkpointId;
        this.checkpointName = checkpointName;
    }
}

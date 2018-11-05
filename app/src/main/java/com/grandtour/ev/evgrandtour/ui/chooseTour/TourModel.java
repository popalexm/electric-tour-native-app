package com.grandtour.ev.evgrandtour.ui.chooseTour;

import android.support.annotation.NonNull;

public class TourModel {

    @NonNull
    public final String tourId;
    public final String positionInList;
    @NonNull
    public final String tourName;
    @NonNull
    public final TourClickedListener listener;

    TourModel(@NonNull String tourId, String positionInList, @NonNull String tourName, @NonNull TourClickedListener callback) {
        this.tourId = tourId;
        this.positionInList = positionInList;
        this.tourName = tourName;
        this.listener = callback;
    }
}

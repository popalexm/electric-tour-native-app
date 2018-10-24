package com.grandtour.ev.evgrandtour.ui.mapsView.distancePickerDialog;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

public class DistancePickerContract extends BaseContract {

    public interface View extends BaseContract.View {

        void displayDistance(@NonNull Integer distance);

        void calculateDistances();

        void dismissDialog();
    }

    public interface Presenter extends BaseContract.Presenter {

        void onCalculateDistanceBetweenCheckpoints();

        void onDismissButtonClicked();

        void startRouteCalculations(@NonNull Integer start, @NonNull Integer end);

    }
}

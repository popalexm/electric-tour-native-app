package com.grandtour.ev.evgrandtour.ui.distancePicker;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

public class DistancePickerContract extends BaseContract {

    public interface View extends BaseContract.View {

        void displayDistance(@NonNull Integer distance, String duration);

        void calculateDistances();

        void dismissDialog();
    }

    public interface Presenter extends BaseContract.Presenter {

        void onCalculateDistanceBetweenCheckpoints();

        void onDismissButtonClicked();

        void onCalculateRouteInformationClicked(@NonNull Integer start, @NonNull Integer end);

    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import androidx.annotation.NonNull;

public class CreateNewTripContract {

    public interface View extends BaseContract.View {

        void displayErrorOnTripNameField(@NonNull String error);

        void removeErrorOnTripNameField();

        void moveToTripCheckpointsPlanningScreen(int tripId);
    }

    public interface Presenter extends BaseContract.Presenter {

        void onCheckForPreviousInPlanningTrip();

        void onNextPressed(@NonNull String newTripName, @NonNull String newTripDescription);
    }

}

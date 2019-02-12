package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;

public class NewTripCheckpointDetailsFragmentContract {

    public interface View extends BaseContract.View {

        void dismissDetailsDialog();

        void shakeCheckpointNameTextView();

    }

    public interface Presenter extends BaseContract.Presenter, SwitchCheckedStatusCallback {

        void onInitCallbackToParentFragment(@NonNull AddNewCheckpointDetailsCallback checkpointDetailsCallback);

        void onRetrievedTripCheckpointDetailsFromBundle(@NonNull TripCheckpoint tripCheckpoint);

        void onSaveCheckpointDetailsClicked(@NonNull String checkpointName, @NonNull String checkpointDescription);

        void onDismissButtonClicked();
    }

}

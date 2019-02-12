package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CompoundButton;

public class NewTripCheckpointDetailsFragmentPresenter extends BasePresenter implements NewTripCheckpointDetailsFragmentContract.Presenter {

    @NonNull
    private final NewTripCheckpointDetailsFragmentContract.View view;
    private boolean areArrivalNotificationsEnabled;
    private boolean areDepartureNotificationsEnabled;
    @Nullable
    private AddNewCheckpointDetailsCallback checkpointDetailsCallback;
    @Nullable
    private TripCheckpoint tripCheckpoint;

    NewTripCheckpointDetailsFragmentPresenter(@NonNull NewTripCheckpointDetailsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onInitCallbackToParentFragment(@NonNull AddNewCheckpointDetailsCallback checkpointDetailsCallback) {
        this.checkpointDetailsCallback = checkpointDetailsCallback;
    }

    @Override
    public void onRetrievedTripCheckpointDetailsFromBundle(@NonNull TripCheckpoint tripCheckpoint) {
        this.tripCheckpoint = tripCheckpoint;
    }

    @Override
    public void onSaveCheckpointDetailsClicked(@NonNull String checkpointName, @NonNull String checkpointDescription) {
        if (isViewAttached && TextUtils.isEmpty(checkpointName)) {
            view.shakeCheckpointNameTextView();
        } else if (tripCheckpoint != null && isViewAttached) {
            tripCheckpoint.setCheckpointTitle(checkpointName);
            tripCheckpoint.setCheckpointDescription(checkpointDescription);
            tripCheckpoint.setAreArrivalNotificationsEnabled(areArrivalNotificationsEnabled);
            tripCheckpoint.setAreDepartureNotificationsEnabled(areDepartureNotificationsEnabled);
            if (checkpointDetailsCallback != null) {
                checkpointDetailsCallback.onCheckpointDetailsAdded(tripCheckpoint);
            }
            view.dismissDetailsDialog();
        }
    }

    @Override
    public void onDismissButtonClicked() {
        if (isViewAttached) {
            view.dismissDetailsDialog();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchArrivalNotifications:
                areArrivalNotificationsEnabled = isChecked;
                break;
            case R.id.switchDepartureNotifications:
                areDepartureNotificationsEnabled = isChecked;
                break;
        }
    }
}

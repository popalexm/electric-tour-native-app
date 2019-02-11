package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.CompoundButton;

public class NewTripCheckpointDetailsFragmentPresenter extends BasePresenter implements NewTripCheckpointDetailsFragmentContract.Presenter {

    @NonNull
    private final NewTripCheckpointDetailsFragmentContract.View view;
    private boolean areArrivalNotificationsEnabled;
    private boolean areDepartureNotificationsEnabled;

    NewTripCheckpointDetailsFragmentPresenter(@NonNull NewTripCheckpointDetailsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onSaveCheckpointDetailsClicked(@NonNull String checkpointName, @NonNull String checkpointDescription) {
        if (isViewAttached && TextUtils.isEmpty(checkpointName)) {
            view.shakeCheckpointNameTextView();
        } else {
            // TODO Add a callback to the main Add Trip Fragment
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

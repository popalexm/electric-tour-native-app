package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter {

    @NonNull
    private final PlanNewTripContract.View view;

    @NonNull
    private final List<TripCheckpoint> plannedTripCheckpoints = new ArrayList<>();

    PlanNewTripPresenter(@NonNull PlanNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapLocationClicked(@NonNull LatLng clickedLocation) {
        if (isViewAttached) {
            view.openNewCheckpointDetailsDialog(clickedLocation);
        }
    }

    @Override
    public void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint) {
        plannedTripCheckpoints.add(tripCheckpoint);
        if (isViewAttached) {
            view.displayTripCheckpointOnMap(tripCheckpoint);
        }
    }

    @Override
    public void onDeleteCheckpointFromTrip(int checkpointId) {
        // TODO Implemented checkpoint removal logic via id
    }

    @Override
    public void onMyLocationButtonClicked() {

    }

    @Override
    public void onFinalizeTripPlanningClicked(@NonNull String tripTitle) {
        if (isViewAttached) {
            if (TextUtils.isEmpty(tripTitle)) {
                view.displayInvalidTripNameWarning();
            } else if (plannedTripCheckpoints.size() < 2) {
                view.showMessage(Injection.provideResources()
                        .getString(R.string.message_please_add_at_least_tqo_checkpoints_to_trip));
            } else {
                // TODO Implement database caching and API calling logic
            }
        }
    }
}

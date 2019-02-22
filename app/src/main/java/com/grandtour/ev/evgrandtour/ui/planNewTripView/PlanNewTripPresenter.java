package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.LoadInPlanningTripUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.SaveInPlanningTripCheckpointUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class PlanNewTripPresenter extends BasePresenter implements PlanNewTripContract.Presenter {

    @NonNull
    private final PlanNewTripContract.View view;

    @Nullable
    private InPlanningTripDetails inPlanningTripDetails;

    PlanNewTripPresenter(@NonNull PlanNewTripContract.View view) {
        this.view = view;
    }

    @Override
    public void onMapReady() {
        addSubscription(new LoadInPlanningTripUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                .doOnNext(inPlanningTrip -> {
                    inPlanningTripDetails = inPlanningTrip;
                    loadPreviouslyPlannedTripDetails(inPlanningTrip);
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe());
    }

    @Override
    public void onMapLocationClicked(@NonNull LatLng clickedLocation) {
        if (isViewAttached) {
            view.openNewCheckpointDetailsDialog(clickedLocation);
        }
    }

    @Override
    public void onNewTripCheckpointAdded(@NonNull TripCheckpoint tripCheckpoint) {
        if (inPlanningTripDetails != null && inPlanningTripDetails.getPlannedTripCheckpoints() != null) {
            addSubscription(
                    new SaveInPlanningTripCheckpointUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager(), tripCheckpoint,
                            inPlanningTripDetails.getInPlanningTripId()).perform()
                            .doOnNext(tripCheckpoint1 -> {
                                if (isViewAttached) {
                                    inPlanningTripDetails.getPlannedTripCheckpoints()
                                            .add(tripCheckpoint);
                                    view.displayNewTripCheckpointOnMap(tripCheckpoint);
                                }
                            })
                            .doOnError(Throwable::printStackTrace)
                            .subscribe());
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
            } else if (inPlanningTripDetails != null && inPlanningTripDetails.getPlannedTripCheckpoints() != null &&
                    inPlanningTripDetails.getPlannedTripCheckpoints()
                            .size() < 2) {
                view.showMessage(Injection.provideResources()
                        .getString(R.string.message_please_add_at_least_tqo_checkpoints_to_trip));
            } else {
                // TODO Implement database caching and API calling logic
            }
        }
    }

    private void loadPreviouslyPlannedTripDetails(@NonNull InPlanningTripDetails inPlanningTripDetails) {
        if (isViewAttached && inPlanningTripDetails.getPlannedTripCheckpoints() != null) {
            view.displayPreviousTripCheckpointList(inPlanningTripDetails.getPlannedTripCheckpoints());
            String name = inPlanningTripDetails.getInPlanningTripName();
            String description = inPlanningTripDetails.getInPlanningTripDescription();
            if (name != null && description != null) {
                view.displayPreviousTripNameAndDescription(name, description);
            }
        }
    }
}

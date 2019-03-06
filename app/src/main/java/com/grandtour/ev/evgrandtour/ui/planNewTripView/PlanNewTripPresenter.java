package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.domain.models.PlannedTripStatus;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.DeleteInPlanningCheckpointUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.LoadInPlanningTripDetailsUseCase;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.MoveInPlanningTripToPlannedStatus;
import com.grandtour.ev.evgrandtour.domain.useCases.planNewTripModule.SaveInPlanningTripCheckpointUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.InPlanningTripDetails;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
        addSubscription(new LoadInPlanningTripDetailsUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideStorageManager()).perform()
                .doOnNext(inPlanningTrip -> {
                    inPlanningTripDetails = inPlanningTrip;
                    loadPlannedTripOnMainMapView(inPlanningTrip);
                    loadCheckpointsInReorderingList(inPlanningTripDetails);
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
        if (inPlanningTripDetails != null) {
            addSubscription(new SaveInPlanningTripCheckpointUseCase(Injection.provideRxSchedulers()
                    .getDefault(), Injection.provideStorageManager(), tripCheckpoint, inPlanningTripDetails.getInPlanningTripId()).perform()
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
    public void onDeleteCheckpointFromTrip(@NonNull TripCheckpoint tripCheckpoint) {
        addSubscription(new DeleteInPlanningCheckpointUseCase(Injection.provideRxSchedulers()
                .getDefault(), Injection.provideStorageManager(), tripCheckpoint.getCheckpointId()).perform()
                .doOnComplete(() -> {
                    if (isViewAttached) {
                        view.removeAddedTripCheckpoint(tripCheckpoint);
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe());
    }

    @Override
    public void onMyLocationButtonClicked() {

    }

    @Override
    public void onFinalizeTripPlanningClicked(@NonNull String tripTitle) {
        if (isViewAttached && inPlanningTripDetails != null) {
            if (TextUtils.isEmpty(tripTitle)) {
                view.displayInvalidTripNameWarning();
            } else if (inPlanningTripDetails.getPlannedTripCheckpoints()
                    .size() < 2) {
                view.showMessage(Injection.provideResources()
                        .getString(R.string.message_please_add_at_least_tqo_checkpoints_to_trip));
            } else {
                inPlanningTripDetails.setInPlanningTripName(tripTitle);
                addSubscription(new MoveInPlanningTripToPlannedStatus(Injection.provideRxSchedulers()
                        .getDefault(), Injection.provideStorageManager(), inPlanningTripDetails).perform()
                        .doOnNext(plannedTripStatus -> {
                            if (isViewAttached) {
                                if (plannedTripStatus.equals(PlannedTripStatus.STATUS_SAVED_LOCALLY_SUCCESSFULLY)) {
                                    view.showMessage(Injection.provideResources()
                                            .getString(R.string.message_successfully_saved_trip));
                                } else {
                                    view.showMessage(Injection.provideResources()
                                            .getString(R.string.message_error_saving_trip));
                                }
                            }
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
            }
        }
    }

    private void loadPlannedTripOnMainMapView(@NonNull InPlanningTripDetails tripDetails) {
        if (isViewAttached) {
            view.displayPlannedTripCheckpointsOnMapView(tripDetails.getPlannedTripCheckpoints());
            view.displayPlannedTripNameAndDescription(tripDetails.getInPlanningTripName(), tripDetails.getInPlanningTripDescription());
        }
    }

    private void loadCheckpointsInReorderingList(@NonNull InPlanningTripDetails tripDetails) {
        // List<TripCheckpoint> tripCheckpointListModelReordering = new ArrayList<>();
      /*  for (TripCheckpoint checkpoint : tripDetails.getPlannedTripCheckpoints()){
            TripCheckpointReorderingModel tripCheckpointReorderingModel = new TripCheckpointReorderingModel(checkpoint.getCheckpointId(), String.valueOf(2)
                    ,checkpoint.getCheckpointTitle(), checkpoint.getCheckpointAddress());
            tripCheckpointListModelReordering.add(tripCheckpointReorderingModel);
        } */
        if (isViewAttached) {
            view.displayTripCheckpointsInReorderingList(tripDetails.getPlannedTripCheckpoints());
        }
    }
}

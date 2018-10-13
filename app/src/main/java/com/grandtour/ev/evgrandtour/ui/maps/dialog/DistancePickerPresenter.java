package com.grandtour.ev.evgrandtour.ui.maps.dialog;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.domain.useCases.CalculateDistanceBetweenTwoCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DistancePickerPresenter extends BasePresenter implements DistancePickerContract.Presenter {

    @NonNull
    private final DistancePickerDialogFragment view;

    DistancePickerPresenter(@NonNull DistancePickerDialogFragment view) {
        this.view = view;
    }

    @Override
    public void onCalculateDistanceBetweenCheckpoints() {
        view.calculateDistances();
    }

    @Override
    public void onDismissButtonClicked() {
        if (isViewAttached) {
            view.dismissDialog();
        }
    }

    @Override
    public void startRouteCalculations(@NonNull Integer startCheckpointId, @NonNull Integer endCheckpointId) {
        CalculateDistanceBetweenTwoCheckpointsUseCase calculateDistance = new CalculateDistanceBetweenTwoCheckpointsUseCase(Schedulers.io(),
                AndroidSchedulers.mainThread(), Injection.provideStorageManager(), startCheckpointId, endCheckpointId);
        addSubscription(calculateDistance.perform()
                .doOnSuccess(view::displayDistance)
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    if (isViewAttached) {
                        view.showMessage("Could not calculate routes, please make sure the selected end checkpoint is set after the start checkpoint!");
                    }
                })
                .subscribe());
    }
}

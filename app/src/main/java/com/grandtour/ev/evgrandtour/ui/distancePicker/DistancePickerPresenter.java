package com.grandtour.ev.evgrandtour.ui.distancePicker;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.domain.useCases.GetDrivingInfoBetweenTwoCheckpointsUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;
import com.grandtour.ev.evgrandtour.ui.utils.TimeUtils;

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
    public void onCalculateRouteInformationClicked(@NonNull Integer startCheckpointId, @NonNull Integer endCheckpointId) {
        if (startCheckpointId < endCheckpointId) {
            GetDrivingInfoBetweenTwoCheckpointsUseCase calculateDistance = new GetDrivingInfoBetweenTwoCheckpointsUseCase(Schedulers.io(),
                    AndroidSchedulers.mainThread(), Injection.provideStorageManager(), startCheckpointId, endCheckpointId - 1);
            addSubscription(calculateDistance.perform()
                    .doOnSuccess(distanceDurationPair -> {
                        int distance = distanceDurationPair.first;
                        String duration = TimeUtils.convertFromSecondsToFormattedTime(distanceDurationPair.second);
                        int distanceInKm = distance / 1000;
                        view.displayDistance(distanceInKm, duration);
                    })
                    .doOnError(Throwable::printStackTrace)
                    .subscribe());
        } else {
            if (isViewAttached) {
                view.showMessage(Injection.provideGlobalContext()
                        .getString(R.string.error_message_could_not_calculate_routes));
            }
        }


    }
}

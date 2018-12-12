package com.grandtour.ev.evgrandtour.ui.elevationView;

import com.github.mikephil.charting.data.Entry;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadElevationPointsForClickedPolyline;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadElevationPointsForSelectedTourUseCase;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ElevationChartPresenter extends BasePresenter implements ElevationChartContract.Presenter {

    @NonNull
    private final ElevationChartContract.View view;
    @NonNull
    private final LocalStorageManager localStorageManager;

    ElevationChartPresenter(@NonNull ElevationChartContract.View view) {
        this.view = view;
        localStorageManager = Injection.provideStorageManager();
    }

    @Override
    public void onDisplayElevationChartForRouteLeg(@NonNull Integer routeLegId) {
        addSubscription(new LoadElevationPointsForClickedPolyline(Schedulers.io(), AndroidSchedulers.mainThread(), localStorageManager, routeLegId).perform()
                .doOnSuccess(this::displayElevationPoints)
                .subscribe());
    }

    @Override
    public void onDisplayElevationChartForEntireTour() {
        addSubscription(
                new LoadElevationPointsForSelectedTourUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideStorageManager()).perform()
                        .doOnSuccess(this::displayElevationPoints)
                        .subscribe());
    }

    @Override
    public void onDismissButtonClicked() {
        if (isViewAttached) {
            view.dismissDialog();
        }
    }

    private void displayElevationPoints(List<ElevationPoint> elevationPoints) {
        if (isViewAttached) {
            List<Entry> elevationPointEntries = new ArrayList<>();
            for (int i = 0; i < elevationPoints.size(); i++) {

                ElevationPoint elevationPoint = elevationPoints.get(i);

                int startCheckpointId = elevationPoint.getStartCheckpointOrderId();
                elevationPointEntries.add(new Entry(startCheckpointId, (float) elevationPoint
                        .getElevation()));
            }
            view.displayChart(elevationPointEntries);
        }
    }
}

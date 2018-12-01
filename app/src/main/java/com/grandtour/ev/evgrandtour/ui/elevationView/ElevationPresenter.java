package com.grandtour.ev.evgrandtour.ui.elevationView;

import com.github.mikephil.charting.data.Entry;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.domain.useCases.LoadElevationPointsForClickedPolyline;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ElevationPresenter extends BasePresenter implements ElevationContract.Presenter {

    @NonNull
    private final ElevationContract.View view;
    @NonNull
    private final LocalStorageManager localStorageManager;

    ElevationPresenter(@NonNull ElevationContract.View view) {
        this.view = view;
        localStorageManager = Injection.provideStorageManager();
    }

    @Override
    public void onFragmentReady(@NonNull Integer routeLegId) {
        addSubscription(new LoadElevationPointsForClickedPolyline(Schedulers.io(), AndroidSchedulers.mainThread(), localStorageManager, routeLegId).perform()
                .doOnSuccess(elevationPoints -> {
                    if (isViewAttached) {
                        List<Entry> elevationPointEntries = new ArrayList<>();
                        for (int i = 0; i < elevationPoints.size(); i++) {
                            elevationPointEntries.add(new Entry(i, (float) elevationPoints.get(i)
                                    .getElevation()));
                        }
                        view.displayChart(elevationPointEntries);
                    }
                })
                .subscribe());
    }

    @Override
    public void onDismissButtonClicked() {
        if (isViewAttached) {
            view.dismissDialog();
        }
    }
}

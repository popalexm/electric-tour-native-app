package com.grandtour.ev.evgrandtour.ui.mapsView.chooseTour;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.useCases.SyncAllAvailableToursUseCase;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ChooseTourDialogPresenter implements ChooseTourDialogContract.Presenter {

    @NonNull
    private final ChooseTourDialogContract.View view;
    @NonNull
    private final List<TourDataResponse> tourDataResponses = new ArrayList<>();
    private boolean isViewAttached;

    ChooseTourDialogPresenter(@NonNull ChooseTourDialogContract.View view) {
        this.view = view;
    }

    @Override
    public void onAttach() {
        isViewAttached = true;
        retrieveAvailableToursFromRemoteSource();
    }

    @Override
    public void onDetach() {
        isViewAttached = false;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onTourChosen() {

    }

    @Override
    public void onDismissButtonClicked() {
        view.dismissDialog();
    }

    private void retrieveAvailableToursFromRemoteSource() {
        tourDataResponses.clear();
        new SyncAllAvailableToursUseCase(Schedulers.io(), AndroidSchedulers.mainThread(), Injection.provideBackendApi()).perform()
                .doOnSubscribe(subscription -> {
                    if (isViewAttached) {
                        view.showLoadingView(true);
                    }
                })
                .doOnEach(new Subscriber<Response<TourDataResponse>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                    }

                    @Override
                    public void onNext(Response<TourDataResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tourDataResponses.add(response.body());
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                })
                .doOnComplete(() -> {
                    if (isViewAttached) {
                        view.showLoadingView(false);
                        convertResponseAndLoad();
                    }
                })
                .subscribe();
    }

    private void convertResponseAndLoad() {
        List<TourModel> tourModels = new ArrayList<>();
        for (TourDataResponse response : tourDataResponses) {
            String position = String.valueOf(tourDataResponses.indexOf(response));
            tourModels.add(new TourModel(response.getId(), position, response.getName(), this));
        }
        view.loadAvailableTours(tourModels);
    }

    @Override
    public void OnTourClicked(String tourId) {

    }
}

package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.AvailableToursResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseFlowable;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import retrofit2.Response;

public class SyncAllAvailableToursUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    @NonNull
    private final BackendAPI backendAPI;

    public SyncAllAvailableToursUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI) {
        super(executorThread, postExecutionThread);
        this.backendAPI = backendAPI;
    }

    @Override
    public Flowable<Response<TourDataResponse>> perform() {
     return backendAPI.getAllTours()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .toFlowable()
                .switchMap(new Function<Response<List<AvailableToursResponse>>, Flowable<Response<TourDataResponse>>>() {
            @Override
            public Flowable<Response<TourDataResponse>> apply(Response<List<AvailableToursResponse>> listResponse) throws Exception {
                List<Maybe<Response<TourDataResponse>>> tourRequests = new ArrayList<>();
                if (listResponse.body() != null ){
                    List<AvailableToursResponse> tours = listResponse.body();
                    for (AvailableToursResponse toursResponse : tours) {
                        Maybe<Response<TourDataResponse>> tourRequest = backendAPI.getTourById(toursResponse.getId())
                                .subscribeOn(executorThread)
                                .observeOn(postExecutionThread);
                        tourRequests.add(tourRequest);
                    }
                }
                return Maybe.concat(tourRequests);
            }
        });
    }
}
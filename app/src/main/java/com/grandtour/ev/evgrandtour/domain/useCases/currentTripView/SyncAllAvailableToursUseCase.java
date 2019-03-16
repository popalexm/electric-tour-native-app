package com.grandtour.ev.evgrandtour.domain.useCases.currentTripView;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.AvailableToursResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import retrofit2.Response;

public class SyncAllAvailableToursUseCase extends BaseUseCase implements UseCaseDefinition {

    @NonNull
    private static String USER_TOKEN = "user_token";

    @NonNull
    private final BackendAPI backendAPI;

    public SyncAllAvailableToursUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI) {
        super(executorThread, postExecutionThread);
        this.backendAPI = backendAPI;
    }

    @Override
    public Flowable<Response<TourDataResponse>> perform() {
        String accessToken = Injection.provideSharedPreferences()
                .getString(SyncAllAvailableToursUseCase.USER_TOKEN, "");
        return backendAPI.getAllTours(accessToken)
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
                        Maybe<Response<TourDataResponse>> tourRequest = backendAPI.getTourById(accessToken, toursResponse.getId())
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

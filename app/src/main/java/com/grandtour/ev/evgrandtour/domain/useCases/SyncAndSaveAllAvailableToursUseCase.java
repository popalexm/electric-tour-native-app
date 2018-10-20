package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Tour;
import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.AvailableToursResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import retrofit2.Response;

public class SyncAndSaveAllAvailableToursUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final BackendAPI backendAPI;
    @NonNull
    private final LocalStorageManager localStorageManager;

    public SyncAndSaveAllAvailableToursUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI,
            @NonNull LocalStorageManager localStorageManager) {
        super(executorThread, postExecutionThread);
        this.backendAPI = backendAPI;
        this.localStorageManager = localStorageManager;
    }

    @Override
    public Maybe<long[]> perform() {
        return backendAPI.getAllTours()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<Response<List<AvailableToursResponse>>, Maybe<long[]>>() {
                    @Override
                    public Maybe<long[]> apply(Response<List<AvailableToursResponse>> response) {
                        List<AvailableToursResponse> availableToursResponses = response.body();
                        List<Tour> tours = new ArrayList<>();
                        for (AvailableToursResponse availableToursResponse : availableToursResponses) {
                            Tour tour = new Tour();
                            tour.setTourId(availableToursResponse.getId());
                            tour.setName(availableToursResponse.getName());
                            tours.add(tour);
                        }
                        return Maybe.create(emitter -> {
                            try {
                                long[] ids = localStorageManager.tourDao()
                                        .insert(tours);
                                emitter.onSuccess(ids);
                            } catch (Throwable t) {
                                emitter.onError(t);
                            }
                        });
                    }
                });
    }
}

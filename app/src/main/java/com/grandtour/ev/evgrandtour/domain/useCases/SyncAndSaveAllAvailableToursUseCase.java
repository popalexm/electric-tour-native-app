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
    private final LocalStorageManager storageManager;

    public SyncAndSaveAllAvailableToursUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI,
            @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.backendAPI = backendAPI;
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<long[]> perform() {
        return backendAPI.getAllTours()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<Response<List<AvailableToursResponse>>, Maybe<long[]>>() {
                    @Override
                    public Maybe<long[]> apply(Response<List<AvailableToursResponse>> response) {
                        List<AvailableToursResponse> toursResponses = response.body();
                        if (toursResponses != null) {
                            storageManager.checkpointsDao()
                                    .deleteAll();
                            storageManager.tourDao()
                                    .deleteAll();
                            storageManager.routeDao()
                                    .deleteAll();
                            storageManager.routeWaypointsDao()
                                    .deleteAll();

                            List<Tour> tours = new ArrayList<>();
                            for (AvailableToursResponse tourResponse : toursResponses) {
                                Tour tour = new Tour();
                                tour.setTourId(tourResponse.getId());
                                tour.setName(tourResponse.getName());
                                tours.add(tour);
                            }
                            return Maybe.create(emitter -> {
                                try {
                                    long[] ids = storageManager.tourDao()
                                            .insert(tours);
                                    emitter.onSuccess(ids);
                                } catch (Throwable t) {
                                    emitter.onError(t);
                                }
                            });
                        } else {
                            return null;
                        }
                    }
                });
    }

}

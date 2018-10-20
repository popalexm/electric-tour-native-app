package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourCheckpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseFlowable;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import retrofit2.Response;

public class SyncAndSaveTourDetailsUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    @NonNull
    private final BackendAPI backendAPI;
    @NonNull
    private final LocalStorageManager localStorageManager;
    @NonNull
    private final List<String> tourIdList;

    public SyncAndSaveTourDetailsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI,
            @NonNull LocalStorageManager localStorageManager, @NonNull List<String> tourIdList) {
        super(executorThread, postExecutionThread);
        this.backendAPI = backendAPI;
        this.localStorageManager = localStorageManager;
        this.tourIdList = tourIdList;
    }

    @Override
    public Flowable<Response<TourDataResponse>> perform() {
        List<Maybe<Response<TourDataResponse>>> tourRequests = new ArrayList<>();
        for (String tourId : tourIdList) {
            Maybe<Response<TourDataResponse>> tourRequest = backendAPI.getTourById(tourId)
                    .subscribeOn(executorThread)
                    .observeOn(postExecutionThread)
                    .doOnError(Throwable::printStackTrace)
                    .doOnSuccess(tourDataResponseResponse -> {
                        TourDataResponse response = tourDataResponseResponse.body();
                        if (response != null) {
                            List<TourCheckpoint> importedCheckpoints = response.getTourCheckpoints();
                            try {
                                List<Checkpoint> toSaveCheckpoints = convertToCheckpointObjects(importedCheckpoints, tourId);
                                localStorageManager.checkpointsDao()
                                        .insert(toSaveCheckpoints);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            tourRequests.add(tourRequest);
        }
        return Maybe.concat(tourRequests);
    }

    private List<Checkpoint> convertToCheckpointObjects(@NonNull Iterable<TourCheckpoint> importedCheckpoints, @NonNull String tourId)
            throws NullPointerException {
        List<Checkpoint> toSaveCheckpoints = new ArrayList<>();
        for (TourCheckpoint tourCheckpoint : importedCheckpoints) {
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setCheckpointName(tourCheckpoint.getDescription());
            checkpoint.setTourId(tourId);
            checkpoint.setLatitude(tourCheckpoint.getLatitude());
            checkpoint.setLongitude(tourCheckpoint.getLongitude());
            toSaveCheckpoints.add(checkpoint);
        }
        return toSaveCheckpoints;
    }
}

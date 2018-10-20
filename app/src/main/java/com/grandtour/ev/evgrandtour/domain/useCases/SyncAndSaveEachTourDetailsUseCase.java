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

public class SyncAndSaveEachTourDetailsUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    @NonNull
    private final BackendAPI backendAPI;
    @NonNull
    private final LocalStorageManager localStorageManager;
    @NonNull
    private final List<String> tourIdList;

    public SyncAndSaveEachTourDetailsUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull BackendAPI backendAPI,
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
                        List<TourCheckpoint> tourDataResponses = tourDataResponseResponse.body()
                                .getTourCheckpoints();
                        List<Checkpoint> checkpoints = new ArrayList<>();
                        for (TourCheckpoint tourCheckpoint : tourDataResponses) {
                            {
                                Checkpoint checkpoint = new Checkpoint();
                                checkpoint.setCheckpointName(tourCheckpoint.getDescription());
                                checkpoint.setTourId(tourId);
                                checkpoint.setLatitude(tourCheckpoint.getLatitude());
                                checkpoint.setLongitude(tourCheckpoint.getLongitude());
                                checkpoints.add(checkpoint);
                            }
                        }
                        localStorageManager.checkpointsDao()
                                .insert(checkpoints);
                    });
            tourRequests.add(tourRequest);
        }
        return Maybe.concat(tourRequests);
    }
}

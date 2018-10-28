package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.Tour;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourCheckpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseCompletable;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class SaveToursDataLocallyUseCase extends BaseUseCase implements BaseUseCaseCompletable {

    @NonNull
    private final LocalStorageManager localStorageManager;
    @NonNull
    private final  List<TourDataResponse> tourDataList;

    public SaveToursDataLocallyUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull List<TourDataResponse> tourDataList) {
        super(executorThread, postExecutionThread);
        this.localStorageManager = localStorageManager;
        this.tourDataList = tourDataList;
    }

    @Override
    public Completable perform() {
        return Completable.fromAction(() -> {
            localStorageManager.tourDao().deleteAll();
            localStorageManager.routeDao().deleteAll();
            localStorageManager.checkpointsDao().deleteAll();

            List<Tour> tours = new ArrayList<>();
            for (TourDataResponse response : tourDataList) {
                Tour tour = new Tour();
                tour.setTourId(response.getId());
                tour.setName(response.getName());
                tours.add(tour);
            }
            localStorageManager.tourDao().insert(tours);

            for (TourDataResponse response : tourDataList) {
                String tourId = response.getId();
                List<TourCheckpoint> tourResponseCheckpoints = response.getTourCheckpoints();
                try {
                    List<Checkpoint> toSaveCheckpoints = SaveToursDataLocallyUseCase.convertToCheckpoints(tourResponseCheckpoints, tourId);
                    localStorageManager.checkpointsDao()
                            .insert(toSaveCheckpoints);
                    localStorageManager.checkpointsDao().insert(toSaveCheckpoints);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @NonNull
    private static List<Checkpoint> convertToCheckpoints(@NonNull Iterable<TourCheckpoint> tourResponseCheckpoints, @NonNull String tourId)
            throws NullPointerException {
        List<Checkpoint> toSaveCheckpoints = new ArrayList<>();
        for (TourCheckpoint tourCheckpoint : tourResponseCheckpoints) {
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setOrderInTourId(tourCheckpoint.getTourOrderId());
            checkpoint.setCheckpointName(tourCheckpoint.getDescription());
            checkpoint.setTourId(tourId);
            checkpoint.setLatitude(tourCheckpoint.getLatitude());
            checkpoint.setLongitude(tourCheckpoint.getLongitude());
            toSaveCheckpoints.add(checkpoint);
        }
        return toSaveCheckpoints;
    }
}


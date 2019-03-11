package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.Tour;
import com.grandtour.ev.evgrandtour.data.network.NetworkResponseConverter;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourCheckpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.UseCaseDefinition;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Scheduler;

public class SaveToursDataLocallyUseCase extends BaseUseCase implements UseCaseDefinition {

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
            purgeEntireDatabase();

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
                    List<Checkpoint> toSaveCheckpoints = NetworkResponseConverter.convertResponseToCheckpoints(tourResponseCheckpoints, tourId);
                    localStorageManager.checkpointsDao().insert(toSaveCheckpoints);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Deletes entire database
     */
    private void purgeEntireDatabase() {
        localStorageManager.tourDao()
                .deleteAll();
        localStorageManager.routeDao()
                .deleteAll();
        localStorageManager.checkpointsDao()
                .deleteAll();
        localStorageManager.routeLegDao()
                .deleteAll();
        localStorageManager.routeStepDao()
                .deleteAll();
        localStorageManager.elevationPointDao()
                .deleteAll();
    }
}


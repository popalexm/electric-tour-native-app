package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class GetAvailableRoutesUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;

    public GetAvailableRoutesUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread, @NonNull LocalStorageManager storageManager) {
        super(executorThread, postExecutionThread);
        this.storageManager = storageManager;
    }

    @Override
    public Maybe<List<Route>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTour()
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread)
                .flatMap(new Function<String, MaybeSource<List<Route>>>() {
                    @Override
                    public MaybeSource<List<Route>> apply(String tourId) {
                        return storageManager.routeDao()
                                .getRouteForSelectedTour(tourId)
                                .subscribeOn(executorThread)
                                .observeOn(postExecutionThread);
                    }
                });
    }
}

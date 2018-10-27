package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class SaveRouteToDatabaseUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final String routePolyline;

    public SaveRouteToDatabaseUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull String routePolyline) {
        super(executorThread, postExecutionThread);
        this.storageManager = localStorageManager;
        this.routePolyline = routePolyline;
    }

    @Override
    public Maybe<Long> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(executorThread)
                .observeOn(executorThread)
                .flatMap(new Function<String, MaybeSource<Long>>() {
                    @Override
                    public MaybeSource<Long> apply(String tourId) {
                        return Maybe.fromCallable(new Callable<Long>() {
                            @Override
                            public Long call() {
                                Route route = new Route();
                                route.setTourId(tourId);
                                route.setRoutePolyline(routePolyline);
                                return storageManager.routeDao()
                                        .insert(route);
                            }
                        });
                    }
                });
    }
}

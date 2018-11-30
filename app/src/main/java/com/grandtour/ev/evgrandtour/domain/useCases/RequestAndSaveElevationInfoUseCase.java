package com.grandtour.ev.evgrandtour.domain.useCases;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.network.GoogleMapsAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkRequestBuilders;
import com.grandtour.ev.evgrandtour.data.network.models.response.elevation.ElevationResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.elevation.Result;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseFlowable;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import retrofit2.Response;

public class RequestAndSaveElevationInfoUseCase extends BaseUseCase implements BaseUseCaseFlowable {

    @NonNull
    private final GoogleMapsAPI googleMapsAPI;
    @NonNull
    private final LocalStorageManager localStorageManager;
    @NonNull
    private final List<Long> routeLegIds;

    public RequestAndSaveElevationInfoUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull GoogleMapsAPI googleMapsAPI, @NonNull List<Long> routeLegIds) {
        super(executorThread, postExecutionThread);
        this.googleMapsAPI = googleMapsAPI;
        this.localStorageManager = localStorageManager;
        this.routeLegIds = routeLegIds;
    }

    @Override
    public Flowable<Boolean> perform() {
        return Flowable.fromCallable(new Callable<List<ElevationPoint>>() {
            @Override
            public List<ElevationPoint> call() {
                return localStorageManager.elevationPointDao()
                        .getElevationPointsForRouteLegIds(routeLegIds);
            }
        })
                .flatMap(new Function<List<ElevationPoint>, Flowable<Boolean>>() {
                    @Override
                    public Flowable<Boolean> apply(List<ElevationPoint> elevationPoints) {
                        String apiKey = Injection.provideGlobalContext()
                                .getResources()
                                .getString(R.string.google_maps_key);
                        return createElevationRequest(elevationPoints, apiKey).toFlowable();
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribeOn(executorThread)
                .observeOn(postExecutionThread);
    }

    @NonNull
    private Maybe<Boolean> createElevationRequest(@NonNull List<ElevationPoint> elevationPoints, @NonNull String apiKey) {
        String elevationRequestString = NetworkRequestBuilders.createElevationRequest(elevationPoints);
        return googleMapsAPI.getElevationForCheckpoints(elevationRequestString, apiKey)
                .subscribeOn(executorThread)
                .observeOn(executorThread)
                .flatMap(new Function<Response<ElevationResponse>, MaybeSource<Boolean>>() {
                    @Override
                    public MaybeSource<Boolean> apply(Response<ElevationResponse> elevationResponse) {
                        return Maybe.fromCallable(() -> {
                            ElevationResponse response = elevationResponse.body();
                            if (response != null) {
                                List<Result> elevationResponse1 = response.getResults();
                                for (int index = 0; index < elevationPoints.size(); index++) {
                                    int elevationPointId = elevationPoints.get(index)
                                            .getElevationPointId();
                                    double elevation = elevationResponse1.get(index)
                                            .getElevation();
                                    localStorageManager.elevationPointDao()
                                            .updateElevationPoint(elevationPointId, elevation);
                                }
                                return true;
                            } else {
                                return false;
                            }
                        });
                    }
                });
    }

}

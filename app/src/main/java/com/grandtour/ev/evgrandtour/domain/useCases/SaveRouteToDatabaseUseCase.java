package com.grandtour.ev.evgrandtour.domain.useCases;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.network.NetworkResponseConverter;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Leg;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RouteResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Step;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCase;
import com.grandtour.ev.evgrandtour.domain.base.BaseUseCaseMaybe;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

public class SaveRouteToDatabaseUseCase extends BaseUseCase implements BaseUseCaseMaybe {

    @NonNull
    private final LocalStorageManager storageManager;
    @NonNull
    private final RouteResponse routeResponse;

    public SaveRouteToDatabaseUseCase(@NonNull Scheduler executorThread, @NonNull Scheduler postExecutionThread,
            @NonNull LocalStorageManager localStorageManager, @NonNull RouteResponse routeResponse) {
        super(executorThread, postExecutionThread);
        this.storageManager = localStorageManager;
        this.routeResponse = routeResponse;
    }

    @Override
    public Maybe<List<Long>> perform() {
        return storageManager.tourDao()
                .getCurrentlySelectedTourId()
                .subscribeOn(executorThread)
                .observeOn(executorThread)
                .flatMap(new Function<String, MaybeSource<List<Long>>>() {
                    @Override
                    public MaybeSource<List<Long>> apply(String tourId) {
                        return Maybe.fromCallable(() -> {
                            String routePolyline = routeResponse.getOverviewPolyline()
                                    .getPoints();
                            Route route = NetworkResponseConverter.convertResponseToRoute(routePolyline, tourId);
                            long routeId = storageManager.routeDao()
                                    .insert(route);
                            List<Leg> responseLegs = routeResponse.getLegs();
                            List<Long> routeElevationPoints = new ArrayList<>();

                            for (int i = 0; i < responseLegs.size(); i++) {
                                Leg legResponse = responseLegs.get(i);
                                Pair<Long, List<LatLng>> routeAndElevationPoints = saveRouteLegAndSteps(legResponse, (int) routeId);

                                Long routeLegId = routeAndElevationPoints.first;
                                List<ElevationPoint> elevationPoints = NetworkResponseConverter.convertCoordinatesToElevationPoints(routeLegId,
                                        routeAndElevationPoints.second);

                                storageManager.elevationPointDao()
                                        .insert(elevationPoints);
                                routeElevationPoints.add(routeLegId);
                            }
                            return routeElevationPoints;
                        });
                    }
                });
    }

    /**
     * Saves the legs response and associated steps
     */
    private Pair<Long, List<LatLng>> saveRouteLegAndSteps(@NonNull Leg legResponse, int routeId) {
        RouteLeg routeLeg = NetworkResponseConverter.convertResponseToRouteLeg(legResponse, routeId);
        long routeLegId = storageManager.routeLegDao()
                .insertRouteLeg(routeLeg);

        List<Step> responseSteps = legResponse.getSteps();
        List<RouteStep> routeStepsList = new ArrayList<>();

        List<LatLng> routeLegElevationPoints = new ArrayList<>();
        for (Step stepResponse : responseSteps) {
            RouteStep routeStep = NetworkResponseConverter.convertResponseToRouteStep(stepResponse, (int) routeLegId);
            routeStepsList.add(routeStep);
            List<LatLng> responseStepPoints = PolyUtil.decode(stepResponse.getPolyline()
                    .getPoints());
            List<LatLng> stepElevationPoints = filterPolylineForEachKilometerOfCheckpoints(responseStepPoints);

            routeLegElevationPoints.addAll(stepElevationPoints);
        }
        storageManager.routeStepDao()
                .insertRouteLeg(routeStepsList);
        return new Pair<>(routeLegId, routeLegElevationPoints);
    }

    /**
     * Takes an array of LatLng points extracted from a Polyline
     * and filters them so that there is a distance of 1Km / 1000m between each point
     * This method is used to generate the Elevation points for each Leg of the Route
     */
    @NonNull
    private List<LatLng> filterPolylineForEachKilometerOfCheckpoints(@NonNull List<LatLng> polylinePoints) {
        double maxDistanceBetweenPoints = 1000; // in Meters
        List<LatLng> filteredPolylinePoints = new ArrayList<>();

        double checkpointDistanceBuffer = 0;
        for (int i = 0; i < polylinePoints.size() - 1; i++) {

            LatLng firstCheckpoint = polylinePoints.get(i);
            LatLng secondCheckpoint = polylinePoints.get(i + 1);

            double distanceBetweenPoints = SphericalUtil.computeDistanceBetween(firstCheckpoint, secondCheckpoint);
            if (checkpointDistanceBuffer >= maxDistanceBetweenPoints) {
                filteredPolylinePoints.add(secondCheckpoint);
                checkpointDistanceBuffer = 0;
            } else {
                checkpointDistanceBuffer += distanceBetweenPoints;
            }
        }
        return filteredPolylinePoints;
    }
}

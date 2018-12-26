package com.grandtour.ev.evgrandtour.data.network;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;
import com.grandtour.ev.evgrandtour.data.database.models.Route;
import com.grandtour.ev.evgrandtour.data.database.models.RouteLeg;
import com.grandtour.ev.evgrandtour.data.database.models.RouteStep;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourCheckpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Leg;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Step;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class NetworkResponseConverter {

    private NetworkResponseConverter() {
    }

    @NonNull
    public static Route convertToRouteDatabaseModel(@NonNull String routePolyline, @NonNull String tourId) {
        Route route = new Route();
        route.setTourId(tourId);
        route.setRoutePolyline(routePolyline);
        return route;
    }

    @NonNull
    public static List<Checkpoint> convertResponseToCheckpoints(@NonNull Iterable<TourCheckpoint> tourResponseCheckpoints, @NonNull String tourId)
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

    @NonNull
    public static RouteLeg convertResponseToRouteLeg(@NonNull Leg legResponse, int routeId, int routeStartCheckpointId, int routeEndCheckpointId) {
        RouteLeg routeLeg = new RouteLeg();
        routeLeg.setRouteId(routeId);
        routeLeg.setStartCheckpointId(routeStartCheckpointId);
        routeLeg.setEndCheckpointId(routeEndCheckpointId);

        routeLeg.setRouteLegStartLatitude(legResponse.getStartLocation()
                .getLat());
        routeLeg.setRouteLegStartLongitude(legResponse.getStartLocation()
                .getLng());
        routeLeg.setRouteLegEndLatitude(legResponse.getStartLocation()
                .getLat());
        routeLeg.setRouteLegEndLongitude(legResponse.getStartLocation()
                .getLng());
        return routeLeg;
    }

    @NonNull
    public static RouteStep convertResponseToRouteStep(@NonNull Step stepResponse, int routeLegId) {
        RouteStep routeStep = new RouteStep();
        routeStep.setRouteLegId(routeLegId);
        routeStep.setRouteStepStartLatitude(stepResponse.getStartLocation()
                .getLat());
        routeStep.setRouteStepStartLongitude(stepResponse.getStartLocation()
                .getLng());
        routeStep.setRouteStepEndLatitude(stepResponse.getEndLocation()
                .getLat());
        routeStep.setRouteStepEndLongitude(stepResponse.getEndLocation()
                .getLng());
        routeStep.setRouteStepPolyline(stepResponse.getPolyline()
                .getPoints());
        return routeStep;
    }

    @NonNull
    public static List<ElevationPoint> convertCoordinatesToElevationPoints(long routeLegId, @NonNull Iterable<LatLng> elevationLatLngList,
            int startCheckpointOrderId) {
        List<ElevationPoint> elevationPoints = new ArrayList<>();
        for (LatLng point : elevationLatLngList) {
            ElevationPoint elevationPoint = new ElevationPoint();
            elevationPoint.setRouteLegId((int) routeLegId);
            elevationPoint.setStartCheckpointOrderId(startCheckpointOrderId);
            elevationPoint.setLatitude(point.latitude);
            elevationPoint.setLongitude(point.longitude);
            elevationPoints.add(elevationPoint);
        }
        return elevationPoints;
    }
}

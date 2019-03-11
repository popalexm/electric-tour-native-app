package com.grandtour.ev.evgrandtour.domain.services;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

final class DirectionsElevationServiceUtils {

    private DirectionsElevationServiceUtils() {
    }

    /**
     * Takes an array of LatLng points extracted from a Polyline
     * and filters them so that there is a distance of 2Km / 2000m between each point
     * This method is used to generate the Elevation points for each Leg of the Route
     */
    @NonNull
    static List<LatLng> extractElevationPointsFromPolyline(@NonNull List<LatLng> polylinePoints) {
        double maxDistanceBetweenPoints = 2000; // in Meters
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

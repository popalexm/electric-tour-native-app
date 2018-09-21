package com.grandtour.ev.evgrandtour.ui.utils;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteParameters;
import com.grandtour.ev.evgrandtour.data.persistence.models.Checkpoint;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public final class MapUtils {

    private static final int LOCATION_CIRCLE_RADIUS = 1000;

    @NonNull
    private static final String DIRECTIONS_REQUEST_MODE = "driving";

    private MapUtils() { }

    @NonNull
    public static PolylineOptions generateRoute(@NonNull List<LatLng> mapPoints) {
        PolylineOptions routePolyline = new PolylineOptions();
        routePolyline.color(Color.RED);
        for (LatLng routePoint : mapPoints){
            routePolyline.add(routePoint);
        }
        return routePolyline;
    }

    @NonNull
    public static List<LatLng> convertPolyLineToMapPoints(@NonNull String polyline) {
       return PolyUtil.decode(polyline);
    }

    @NonNull
    public static RouteParameters generateRouteRequestParams(@NonNull List<LatLng> checkpoints) {
        LatLng startCheckpoint = checkpoints.get(0);
        LatLng endCheckpoint = checkpoints.get(checkpoints.size() - 1);
        checkpoints.remove(startCheckpoint);
        checkpoints.remove(endCheckpoint);
        return new RouteParameters.RouteParametersBuilder().setStartWaypoint(startCheckpoint)
                .setEndWaypoint(endCheckpoint)
                .setTransitWaypoints(checkpoints)
                .setMode(MapUtils.DIRECTIONS_REQUEST_MODE)
                .setAPIKey(Injection.provideGlobalContext().getString(R.string.google_maps_key))
                .createRouteParameters();
    }

    @NonNull
    public static List<Pair<Integer, MarkerOptions>> convertCheckpointsToMarkers(@NonNull Iterable<Checkpoint> checkpoints) {
        List<Pair<Integer, MarkerOptions>> markerInfoList = new ArrayList<>();
        for (Checkpoint checkpoint : checkpoints) {
            try {
                double latitude = checkpoint.getLatitude();
                double longitude = checkpoint.getLongitude();
                IconGenerator iconGenerator = new IconGenerator(Injection.provideGlobalContext());
                iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
                Bitmap icon = iconGenerator.makeIcon(String.valueOf(checkpoint.getCheckpointId()));

                Integer distanceToNext = checkpoint.getDistanceToNextCheckpoint();
                if (distanceToNext != null) {
                    distanceToNext = distanceToNext / 1000;
                } else {
                    distanceToNext = 0;
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .snippet("Distance to the next checkpoint : " + String.valueOf(distanceToNext) + " Km")
                        .title(checkpoint.getCheckpointName());

                Pair<Integer, MarkerOptions> markerInfo = new Pair<>(checkpoint.getCheckpointId(), markerOptions);
                markerInfoList.add(markerInfo);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return markerInfoList;
    }

    @NonNull
    public static MarkerOptions getCurrentUserLocationMarker(@NonNull LatLng coordinates) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coordinates);
        markerOptions.title(Injection.provideGlobalContext().getString(R.string.title_marker_current_location));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_light_blue_600_24dp));
        return markerOptions;
    }

    @NonNull
    public static CircleOptions getCurrentUserLocationCircle(@NonNull LatLng latLng) {
        return new CircleOptions()
                .center(latLng)
                .strokeColor(Injection.provideGlobalContext().getResources().getColor(R.color.colorPrimary))
                .radius(MapUtils.LOCATION_CIRCLE_RADIUS);
    }
}

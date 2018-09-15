package com.grandtour.ev.evgrandtour.utils;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteParameters;
import com.grandtour.ev.evgrandtour.data.persistence.models.Waypoint;

import android.graphics.Color;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MapUtils {

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
    public static RouteParameters generateRouteParams(@NonNull LatLng startPosition , @NonNull LatLng endPosition, List<LatLng> transitWaypoints) {
        return new RouteParameters.RouteParametersBuilder()
                .setStartWaypoint(startPosition)
                .setEndWaypoint(endPosition)
                .setTransitWaypoints(transitWaypoints)
                .setMode(MapUtils.DIRECTIONS_REQUEST_MODE)
                .setAPIKey(Injection.provideGlobalContext().getString(R.string.google_maps_key))
                .createRouteParameters();
    }

    @NonNull
    public static List<MarkerOptions> convertWaypointsToMarkers(@NonNull Iterable<Waypoint> waypoints) {
        List<MarkerOptions> markerOptions = new ArrayList<>();
        for (Waypoint waypoint : waypoints) {
            try {
                double latitude = Double.valueOf(waypoint.getLatitude());
                double longitude = Double.valueOf(waypoint.getLongitude());
                markerOptions.add(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_red_500_24dp))
                        .title(waypoint.getWaypointId() + " - " + waypoint.getWaypointName()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return markerOptions;
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
                .radius(100);
    }
}

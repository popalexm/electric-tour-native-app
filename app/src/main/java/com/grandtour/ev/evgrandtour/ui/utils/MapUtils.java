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

import java.util.ArrayList;
import java.util.List;

public final class MapUtils {

    private static final int LOCATION_CIRCLE_RADIUS = 200;

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
    public static List<MarkerOptions> convertWaypointsToMarkers(@NonNull Iterable<Checkpoint> waypoints) {
        List<MarkerOptions> markerOptions = new ArrayList<>();
        for (Checkpoint checkpoint : waypoints) {
            try {
                double latitude = Double.valueOf(checkpoint.getLatitude());
                double longitude = Double.valueOf(checkpoint.getLongitude());
                IconGenerator iconGenerator = new IconGenerator(Injection.provideGlobalContext());
                iconGenerator.setStyle(IconGenerator.STYLE_BLUE);
                Bitmap icon = iconGenerator.makeIcon(String.valueOf(checkpoint.getCheckpointId()));
                markerOptions.add(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .title(checkpoint.getCheckpointName()));
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
                .radius(MapUtils.LOCATION_CIRCLE_RADIUS);
    }
}

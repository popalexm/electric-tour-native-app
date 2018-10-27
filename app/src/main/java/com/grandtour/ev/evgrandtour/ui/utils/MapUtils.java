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
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.request.RouteParameters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public final class MapUtils {

    @NonNull
    private static final String TAG = MapUtils.class.getSimpleName();
    public static final int LOCATION_CIRCLE_RADIUS = 1000;
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
    public static String generateInfoMessage(@NonNull Pair<Pair<Integer, Integer>, String> routeInfoPair) {
        Pair<Integer, Integer> distanceDuration = routeInfoPair.first;
        int lengthInKm = distanceDuration.first / 1000;
        int duration = distanceDuration.second;
        String routeName = routeInfoPair.second;
        String convertedDuration = TimeUtils.convertFromSecondsToFormattedTime(duration);
        Context ctx = Injection.provideGlobalContext();
        return ctx.getString(R.string.format_route_inf_message, routeName + " |", lengthInKm,
                ctx.getString(R.string.suffix_kilometers), convertedDuration);
    }

    @NonNull
    public static String composeUriForMapsIntentRequest(@NonNull List<Checkpoint> designatedCheckpoints) {
        StringBuilder navUriBuilder = new StringBuilder();
        navUriBuilder.append(MapConstant.MAP_URI_PREFIX);
        navUriBuilder.append(MapConstant.MAP_URI_DESTINATION_PREFIX);
        int checkpointsSize = designatedCheckpoints.size();
        Checkpoint destinationCheckpoint = designatedCheckpoints.get(checkpointsSize -1);
        String destinationString = destinationCheckpoint.getLatitude() + "," + destinationCheckpoint.getLongitude();
        navUriBuilder.append(destinationString);
        navUriBuilder.append(MapConstant.MAP_URI_WAYPOINTS_PREFX);
        designatedCheckpoints.remove(designatedCheckpoints.remove(checkpointsSize -1));
        for (int i = 0; i < designatedCheckpoints.size(); i ++) {
            Checkpoint checkpoint = designatedCheckpoints.get(i);
            if (i < designatedCheckpoints.size() - 1) {
                navUriBuilder.append(checkpoint.getLatitude())
                        .append(",")
                        .append(checkpoint.getLongitude()).append("|");
            } else {
                navUriBuilder.append(checkpoint.getLatitude())
                        .append(",")
                        .append(checkpoint.getLongitude());
            }

        }
        Log.i(MapUtils.TAG, "Resulted uri : " + navUriBuilder);
        return navUriBuilder.toString();
    }

    @NonNull
    public static List<Pair<Integer, MarkerOptions>> convertCheckpointsToMarkers(@NonNull Iterable<Checkpoint> checkpoints) {
        List<Pair<Integer, MarkerOptions>> markerInfoList = new ArrayList<>();
        Context ctx = Injection.provideGlobalContext();
        for (Checkpoint checkpoint : checkpoints) {
            try {
                double latitude = checkpoint.getLatitude();
                double longitude = checkpoint.getLongitude();
                IconGenerator iconGenerator = new IconGenerator(Injection.provideGlobalContext());
                iconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
                Bitmap icon = iconGenerator.makeIcon(String.valueOf(checkpoint.getOrderInTourId()));

                Integer distanceToNext = checkpoint.getDistanceToNextCheckpoint();
                Integer durationToNext = checkpoint.getDurationToNextCheckpoint();

                if (distanceToNext != null) {
                    distanceToNext = distanceToNext / 1000;
                } else {
                    distanceToNext = 0;
                }
                String formattedTime = TimeUtils.convertFromSecondsToFormattedTime(durationToNext);
                String markerInfoMessage = ctx.getString(R.string.format_checkpoint_info_window_text,
                        ctx.getString(R.string.message_distance_to_next_checkpoint), distanceToNext, ctx.getString(R.string.suffix_kilometers),
                        ctx.getString(R.string.message_eta_to_next_checkpoint), formattedTime);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .snippet(markerInfoMessage)
                        .title(checkpoint.getCheckpointName());

                Pair<Integer, MarkerOptions> markerInfo = new Pair<>(checkpoint.getCheckpointId(), markerOptions);
                markerInfoList.add(markerInfo);
            } catch (NumberFormatException | NullPointerException exception) {
                exception.printStackTrace();
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

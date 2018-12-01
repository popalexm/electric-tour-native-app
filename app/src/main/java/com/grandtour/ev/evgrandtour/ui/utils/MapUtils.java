package com.grandtour.ev.evgrandtour.ui.utils;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public final class MapUtils {

    @NonNull
    private static final String TAG = MapUtils.class.getSimpleName();

    public static final int LOCATION_CIRCLE_RADIUS = 1000;

    private MapUtils() {
    }

    @NonNull
    public static PolylineOptions generateRoute(@NonNull List<LatLng> mapPoints) {
        PolylineOptions routePolyline = new PolylineOptions();
        routePolyline.width(15);
        routePolyline.color(Injection.provideGlobalContext()
                .getResources()
                .getColor(R.color.colorBlue));
        for (LatLng routePoint : mapPoints) {
            routePolyline.add(routePoint);
        }
        return routePolyline;
    }

    @NonNull
    public static List<LatLng> convertPolyLineToMapPoints(@NonNull String polyline) {
        return PolyUtil.decode(polyline);
    }


    @NonNull
    public static String generateInfoMessage(@NonNull Pair<Pair<Integer, Integer>, String> routeInfoPair) {
        Pair<Integer, Integer> distanceDuration = routeInfoPair.first;
        int lengthInKm = distanceDuration.first / 1000;
        int duration = distanceDuration.second;
        String routeName = routeInfoPair.second;
        String convertedDuration = TimeUtils.convertFromSecondsToFormattedTime(duration);
        Context ctx = Injection.provideGlobalContext();
        return ctx.getString(R.string.format_route_inf_message, routeName, lengthInKm, ctx.getString(R.string.suffix_kilometers), convertedDuration);
    }

    @NonNull
    public static String composeUriForMapsIntentRequest(@NonNull List<Checkpoint> designatedCheckpoints) {
        StringBuilder navUriBuilder = new StringBuilder();
        navUriBuilder.append(MapConstant.MAP_URI_PREFIX);
        navUriBuilder.append(MapConstant.MAP_URI_DESTINATION_PREFIX);
        int checkpointsSize = designatedCheckpoints.size();
        Checkpoint destinationCheckpoint = designatedCheckpoints.get(checkpointsSize - 1);
        String destinationString = destinationCheckpoint.getLatitude() + "," + destinationCheckpoint.getLongitude();
        navUriBuilder.append(destinationString);
        navUriBuilder.append(MapConstant.MAP_URI_WAYPOINTS_PREFX);
        designatedCheckpoints.remove(designatedCheckpoints.remove(checkpointsSize - 1));
        for (int i = 0; i < designatedCheckpoints.size(); i++) {
            Checkpoint checkpoint = designatedCheckpoints.get(i);
            if (i < designatedCheckpoints.size() - 1) {
                navUriBuilder.append(checkpoint.getLatitude())
                        .append(",")
                        .append(checkpoint.getLongitude())
                        .append("|");
            } else {
                navUriBuilder.append(checkpoint.getLatitude())
                        .append(",")
                        .append(checkpoint.getLongitude());
            }

        }
        Log.i(MapUtils.TAG, "Maps intent uri : " + navUriBuilder);
        return navUriBuilder.toString();
    }

    @NonNull
    public static List<MapCheckpoint> convertToMapCheckpoints(@NonNull Iterable<Checkpoint> checkpoints) {
        Context ctx = Injection.provideGlobalContext();
        int markerColor = ctx.getResources()
                .getColor(R.color.colorAccent);
        List<MapCheckpoint> mapCheckpoints = new ArrayList<>();
        for (Checkpoint checkpoint : checkpoints) {
            LatLng position = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
            Integer distanceToNext = checkpoint.getDistanceToNextCheckpoint();
            Integer durationToNext = checkpoint.getDurationToNextCheckpoint();
            String checkpointInfo = MapUtils.getMapCheckpointDetails(ctx, distanceToNext, durationToNext);

            MapCheckpoint mapCheckpoint = new MapCheckpoint(position, checkpoint.getCheckpointId(), checkpoint.getOrderInTourId(),
                    checkpoint.getCheckpointName(), checkpointInfo, markerColor);
            mapCheckpoints.add(mapCheckpoint);
        }
        return mapCheckpoints;
    }

    @NonNull
    private static String getMapCheckpointDetails(@NonNull Context ctx, @Nullable Integer distanceToNext, @Nullable Integer durationToNext) {
        if (distanceToNext != null) {
            distanceToNext = distanceToNext / 1000;
        } else {
            distanceToNext = 0;
        }
        if (durationToNext == null) {
            durationToNext = 0;
        }
        String formattedTime = TimeUtils.convertFromSecondsToFormattedTime(durationToNext);
        return ctx.getString(R.string.format_checkpoint_info_window_text, ctx.getString(R.string.message_distance_to_next_checkpoint), distanceToNext,
                ctx.getString(R.string.suffix_kilometers), ctx.getString(R.string.message_eta_to_next_checkpoint), formattedTime);
    }

    @NonNull
    public static MarkerOptions getCurrentUserLocationMarker(@NonNull LatLng coordinates) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coordinates);
        markerOptions.title(Injection.provideGlobalContext()
                .getString(R.string.title_marker_current_location));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_light_blue_600_24dp));
        return markerOptions;
    }

    @NonNull
    public static CircleOptions getCurrentUserLocationCircle(@NonNull LatLng latLng) {
        return new CircleOptions().center(latLng)
                .strokeColor(Injection.provideGlobalContext()
                        .getResources()
                        .getColor(R.color.colorAccent))
                .radius(MapUtils.LOCATION_CIRCLE_RADIUS);
    }
}

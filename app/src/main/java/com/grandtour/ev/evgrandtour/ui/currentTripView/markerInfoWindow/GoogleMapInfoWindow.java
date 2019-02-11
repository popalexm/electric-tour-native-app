package com.grandtour.ev.evgrandtour.ui.currentTripView.markerInfoWindow;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.R;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

public class GoogleMapInfoWindow implements GoogleMap.InfoWindowAdapter {

    @NonNull
    private static final String KM_PATTERN = "Km";
    @NonNull
    private final Activity context;

    public GoogleMapInfoWindow(@NonNull Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View windowLayout = context.getLayoutInflater()
                .inflate(R.layout.map_marker_info_window, null);
        TextView txtCheckpointName = windowLayout.findViewById(R.id.txtCheckpointName);
        TextView txtCheckpointDistance = windowLayout.findViewById(R.id.txtViewLength);
        TextView txtCheckpointDuration = windowLayout.findViewById(R.id.txtViewDuration);

        String checkpointName = marker.getTitle();
        if (checkpointName != null) {
            txtCheckpointName.setText(marker.getTitle());
        }
        String bodyText = marker.getSnippet();
        if (bodyText != null) {
            int position = bodyText.indexOf(GoogleMapInfoWindow.KM_PATTERN);
            int positionIncludingPattern = position + GoogleMapInfoWindow.KM_PATTERN.length();
            String distanceToNext = bodyText.substring(0, positionIncludingPattern);
            txtCheckpointDistance.setText(distanceToNext);
            String travelTimeToNext = bodyText.substring(positionIncludingPattern);
            txtCheckpointDuration.setText(travelTimeToNext);
        }
        return windowLayout;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

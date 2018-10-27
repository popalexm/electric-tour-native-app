package com.grandtour.ev.evgrandtour.ui.mapsView.markerInfo;

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
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View windowLayout = context.getLayoutInflater()
                .inflate(R.layout.info_window, null);
        TextView txtCheckpointName = windowLayout.findViewById(R.id.txtCheckpointName);
        TextView txtCheckpointLenght = windowLayout.findViewById(R.id.txtViewLength);
        TextView txtCheckpointDuration = windowLayout.findViewById(R.id.txtViewDuration);

        String bodyText = marker.getSnippet();
        txtCheckpointName.setText(marker.getTitle());
        int position = bodyText.indexOf(GoogleMapInfoWindow.KM_PATTERN);
        int positionIncludingPattern = position + GoogleMapInfoWindow.KM_PATTERN.length();
        String distanceToNext = bodyText.substring(0, positionIncludingPattern);
        txtCheckpointLenght.setText(distanceToNext);
        String travelTimeToNext = bodyText.substring(positionIncludingPattern);
        txtCheckpointDuration.setText(travelTimeToNext);
        return windowLayout;
    }


}

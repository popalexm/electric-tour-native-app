package com.grandtour.ev.evgrandtour.ui.mapsView.markerInfo;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GoogleMapInfoWindow implements GoogleMap.InfoWindowAdapter {

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
        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(context);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(context);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);
        return info;
    }
}

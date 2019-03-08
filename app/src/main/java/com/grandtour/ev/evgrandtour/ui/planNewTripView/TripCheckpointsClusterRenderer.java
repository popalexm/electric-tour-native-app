package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class TripCheckpointsClusterRenderer extends DefaultClusterRenderer<TripCheckpoint> {

    @NonNull
    private final IconGenerator iconGenerator;
    @NonNull
    private final IconGenerator clusterIconGenerator;

    public TripCheckpointsClusterRenderer(@NonNull Context context, @NonNull GoogleMap map, @NonNull ClusterManager<TripCheckpoint> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);
        clusterIconGenerator = new IconGenerator(context);
        setupClusterMarkerIcon(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(TripCheckpoint mapCheckpoint, MarkerOptions markerOptions) {
        // String checkpointId = String.valueOf(mapCheckpoint.getOrderInRouteId());
        // int color = mapCheckpoint.getMarkerIconColor();
        //  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(generateMarkerIcon(color, checkpointId)));
        markerOptions.title(mapCheckpoint.getCheckpointTitle());
        markerOptions.snippet(mapCheckpoint.getCheckpointDescription());
        markerOptions.draggable(true);

    }

    @NonNull
    private Bitmap generateMarkerIcon(int color, @NonNull String checkpointId) {
        iconGenerator.setColor(color);
        iconGenerator.setTextAppearance(R.style.markerTextStyle);
        return iconGenerator.makeIcon(checkpointId);
    }

    private void setupClusterMarkerIcon(@NonNull Context context) {
        Drawable background = context.getResources()
                .getDrawable(R.drawable.marker_icon);
        clusterIconGenerator.setTextAppearance(R.style.clusterMarkerTextStyle);
        clusterIconGenerator.setBackground(background);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<TripCheckpoint> cluster, MarkerOptions markerOptions) {
        Bitmap clusterIcon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(clusterIcon))
                .anchor(clusterIconGenerator.getAnchorU(), clusterIconGenerator.getAnchorV());
    }
}

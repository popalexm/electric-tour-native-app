package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.content.Context;

public class TripCheckpointsClusterRenderer extends DefaultClusterRenderer<TripCheckpoint> {

    public TripCheckpointsClusterRenderer(Context context, GoogleMap map, ClusterManager<TripCheckpoint> clusterManager) {
        super(context, map, clusterManager);
    }
}

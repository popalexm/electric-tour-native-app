package com.grandtour.ev.evgrandtour.ui.mainMapsView.models;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import android.content.Context;
import android.support.annotation.NonNull;

public class MapCheckpointClusterRenderer extends DefaultClusterRenderer<MapCheckpoint> {

    public MapCheckpointClusterRenderer(@NonNull Context context, @NonNull GoogleMap map, @NonNull ClusterManager<MapCheckpoint> clusterManager) {
        super(context, map, clusterManager);
    }
}

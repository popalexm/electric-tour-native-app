package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.services.RouteDirectionsRequestsService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class DirectionsRequestsReceiver extends BroadcastReceiver {

    @NonNull
    private final MapsFragmentContract.Presenter presenter;

    public DirectionsRequestsReceiver(@NonNull MapsFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundleContent = intent.getExtras();
            if (bundleContent != null) {
                for (String keySet : bundleContent.keySet()) {
                    switch (keySet) {
                        case RouteDirectionsRequestsService.ROUTE_MAP_POINTS_BUNDLE:
                            ArrayList<LatLng> mapPoints = bundleContent.getParcelableArrayList(RouteDirectionsRequestsService.ROUTE_MAP_POINTS_BUNDLE);
                            if (mapPoints != null) {
                                presenter.onNewRoutesReceived(mapPoints);
                            }
                            break;

                        case RouteDirectionsRequestsService.REQUEST_ERROR_CODE:
                            String errorType = bundleContent.getString(RouteDirectionsRequestsService.REQUEST_ERROR_CODE);
                            if (errorType != null) {
                                presenter.onRoutesRequestsError(errorType);
                            }
                            break;

                        case RouteDirectionsRequestsService.ROUTE_START_REQUESTS_BUNDLE:
                            boolean areRoutesRequestsInProgress = bundleContent.getBoolean(RouteDirectionsRequestsService.ROUTE_START_REQUESTS_BUNDLE);
                            if (areRoutesRequestsInProgress) {
                                presenter.onCalculatingRoutesStarted();
                            } else {
                                presenter.onCalculatingRoutesDone();
                            }
                            break;
                    }
                }
            }
        }
    }
}

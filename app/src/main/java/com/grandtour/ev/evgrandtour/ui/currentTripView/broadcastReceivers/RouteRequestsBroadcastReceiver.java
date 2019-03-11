package com.grandtour.ev.evgrandtour.ui.currentTripView.broadcastReceivers;

import com.grandtour.ev.evgrandtour.ui.currentTripView.CurrentTripFragmentContract;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class RouteRequestsBroadcastReceiver extends BroadcastReceiver {

    @NonNull
    public static final String ROUTE_START_REQUESTS_BUNDLE = "routeDirectionsRequestsStart";
    @NonNull
    public static final String REQUEST_ERROR_CODE = "requestErrorCode";

    @NonNull
    private final CurrentTripFragmentContract.Presenter presenter;

    public RouteRequestsBroadcastReceiver(@NonNull CurrentTripFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundleContent = intent.getExtras();
            if (bundleContent != null) {
                for (String keySet : bundleContent.keySet()) {
                    switch (keySet) {
                        case RouteRequestsBroadcastReceiver.REQUEST_ERROR_CODE:
                            String errorType = bundleContent.getString(RouteRequestsBroadcastReceiver.REQUEST_ERROR_CODE);
                            if (errorType != null) {
                                presenter.onRoutesRequestsError(errorType);
                            }
                            break;

                        case RouteRequestsBroadcastReceiver.ROUTE_START_REQUESTS_BUNDLE:
                            boolean areRoutesRequestsInProgress = bundleContent.getBoolean(RouteRequestsBroadcastReceiver.ROUTE_START_REQUESTS_BUNDLE);
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

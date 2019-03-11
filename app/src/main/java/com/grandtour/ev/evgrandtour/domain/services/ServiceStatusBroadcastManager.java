package com.grandtour.ev.evgrandtour.domain.services;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.NetworkExceptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ServiceStatusBroadcastManager {

    @NonNull
    private static final String ACTION_ROUTE_BROADCAST = "RouteResultsBroadcast";
    @NonNull
    private static final String ACTION_LOCATION_BROADCAST = "LocationResultsBroadcast";

    @NonNull
    private static final String ROUTE_START_REQUESTS_BUNDLE = "routeDirectionsRequestsStart";
    @NonNull
    private static final String REQUEST_ERROR_CODE = "requestErrorCode";
    @NonNull
    private static final String LOCATION_REQUESTS_BUNDLE = "CurrentLocationBundle";

    /* Uses global application context, not  a memory leak */
    @SuppressLint("StaticFieldLeak")
    private static ServiceStatusBroadcastManager sInstance;
    @NonNull
    private final Context context = Injection.provideGlobalContext();

    @NonNull
    public static ServiceStatusBroadcastManager getInstance() {
        if (ServiceStatusBroadcastManager.sInstance == null) {
            ServiceStatusBroadcastManager.sInstance = new ServiceStatusBroadcastManager();
        }
        return ServiceStatusBroadcastManager.sInstance;
    }

    void broadcastLocationCoordinates(@NonNull Location location) {
        Intent intent = new Intent(ServiceStatusBroadcastManager.ACTION_LOCATION_BROADCAST);
        intent.putExtra(ServiceStatusBroadcastManager.LOCATION_REQUESTS_BUNDLE, location);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }

    void broadcastDirectionRequestProgress(boolean areDirectionsRequestsInProgress) {
        Intent intent = new Intent(ServiceStatusBroadcastManager.ACTION_ROUTE_BROADCAST);
        intent.putExtra(ServiceStatusBroadcastManager.ROUTE_START_REQUESTS_BUNDLE, areDirectionsRequestsInProgress);
        LocalBroadcastManager.getInstance(Injection.provideGlobalContext())
                .sendBroadcast(intent);
    }

    void broadcastRequestError(@NonNull NetworkExceptions exceptions) {
        Intent intent = new Intent(ServiceStatusBroadcastManager.ACTION_ROUTE_BROADCAST);
        intent.putExtra(ServiceStatusBroadcastManager.REQUEST_ERROR_CODE, exceptions.name());
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(intent);
    }

}

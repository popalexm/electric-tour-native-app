package com.grandtour.ev.evgrandtour.services;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationUpdatesService extends Service {

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    public static final String ACTION_LOCATION_INFO_BROADCAST = "EVGrandTourBroadcast";
    public static final String LOCATION_EXTRA_INFORMATION = "LocationCoordinates";

    private final IBinder mBinder = new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = LocationUpdatesService.UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int NOTIFICATION_ID = 12345678;

    @Nullable
    private LocationCallback locationCallback;
    @Nullable
    private FusedLocationProviderClient fusedLocationClient;
    @Nullable
    private LocationRequest locationRequest;


    @Override
    public void onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationUpdateCallback();
        createLocationRequest();
        createLocationCallback();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LocationUpdatesService.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(LocationUpdatesService.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback(){
        locationCallback = new LocationUpdateCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Notification notification = NotificationsUtils.createNotification(this, "Location updates are running while app is in the background", "Grand-Tour");
        startForeground(LocationUpdatesService.NOTIFICATION_ID, notification);
        return true;
    }

    public void requestLocationUpdates() {
        try {
            if (fusedLocationClient != null && locationCallback != null) {
                    fusedLocationClient.requestLocationUpdates(locationRequest,
                            locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e) {
            Log.e(LocationUpdatesService.TAG, "Lost location permission. Could not request updates. " + e.getMessage());
        }
    }

    private void broadCastNewLocation(@NonNull Location location) {
        Intent intent = new Intent(LocationUpdatesService.ACTION_LOCATION_INFO_BROADCAST);
        intent.putExtra(LocationUpdatesService.LOCATION_EXTRA_INFORMATION, location);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {

        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    private class LocationUpdateCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                broadCastNewLocation(location);
            }
        }
    }
}

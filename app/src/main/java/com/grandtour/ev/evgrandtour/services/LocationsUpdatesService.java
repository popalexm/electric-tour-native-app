package com.grandtour.ev.evgrandtour.services;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.services.notifications.NotificationsUtils;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationsUpdatesService extends Service {

    @NonNull
    private static final String TAG = LocationsUpdatesService.class.getSimpleName();
    private static final int LOCATION_NOTIFICATION_ID = 13;
    @NonNull
    public static final String ACTION_LOCATION_BROADCAST = "LocationResultsBroadcast";
    @NonNull
    public static final String LOCATION_REQUESTS_BUNDLE = "CurrentLocationBundle";
    @NonNull
    private final IBinder mBinder = new LocationServiceBinder();
    @NonNull
    private final GpsLocationManager gpsLocationManager = GpsLocationManager.getInstance();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return Service.START_NOT_STICKY;
    }

    @Nullable
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
        Notification notification = NotificationsUtils.createNotification(this, getString(R.string.message_location_updates), getString(R.string.app_name));
        startForeground(LocationsUpdatesService.LOCATION_NOTIFICATION_ID, notification);
        return true;
    }

    private void broadcastLocationCoordinates(@NonNull Location location) {
        Intent intent = new Intent(LocationsUpdatesService.ACTION_LOCATION_BROADCAST);
        intent.putExtra(LocationsUpdatesService.LOCATION_REQUESTS_BUNDLE, location);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    private void startLocationUpdates() {
        gpsLocationManager.initLocationClient();
        gpsLocationManager.createLocationRequest();
        gpsLocationManager.setCallback(new LocationUpdateCallback());
        if (ActivityCompat.checkSelfPermission(Injection.provideGlobalContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gpsLocationManager.startRequestingLocationUpdates();
        }
    }

    private class LocationUpdateCallback extends LocationCallback {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                Log.d(LocationsUpdatesService.TAG, "Current user location is at  " + location.getLatitude() + "," + location.getLongitude());
                broadcastLocationCoordinates(location);
            }
        }
    }

    public class LocationServiceBinder extends Binder {

        public LocationsUpdatesService getService() {
            return LocationsUpdatesService.this;
        }
    }
}

package com.grandtour.ev.evgrandtour.domain.services;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.location.GpsLocationManager;
import com.grandtour.ev.evgrandtour.ui.notifications.NotificationsUtils;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationsUpdatesService extends Service {

    @NonNull
    private static final String TAG = LocationsUpdatesService.class.getSimpleName();
    private static final int LOCATION_NOTIFICATION_ID = 13;

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
        Notification notification = NotificationsUtils.createLowPriorityNotification(this, getString(R.string.message_location_updates),
                getString(R.string.app_name));
        startForeground(LocationsUpdatesService.LOCATION_NOTIFICATION_ID, notification);
        return true;
    }

    private void startLocationUpdates() {
        gpsLocationManager.setLocationUpdatesCallback(new LocationUpdateCallback());
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
                ServiceStatusBroadcastManager.getInstance()
                        .broadcastLocationCoordinates(location);
            }
        }
    }

    public class LocationServiceBinder extends Binder {

        public LocationsUpdatesService getService() {
            return LocationsUpdatesService.this;
        }
    }
}

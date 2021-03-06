package com.grandtour.ev.evgrandtour.data.location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.grandtour.ev.evgrandtour.app.Injection;

import android.Manifest;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.util.Log;

public class GpsLocationManager {

    @NonNull
    private static final String TAG = GpsLocationManager.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 120000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = GpsLocationManager.UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    @Nullable
    private static GpsLocationManager instance;
    @Nullable
    private LocationCallback locationCallback;
    @Nullable
    private FusedLocationProviderClient fusedLocationClient;
    @Nullable
    private LocationRequest locationRequest;

    public static GpsLocationManager getInstance() {
        if (GpsLocationManager.instance == null) {
            GpsLocationManager.instance = new GpsLocationManager();
        }
        return GpsLocationManager.instance;
    }

    private void initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Injection.provideGlobalContext());
    }

    private void createLocationUpdatesRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(GpsLocationManager.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(GpsLocationManager.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void setLocationUpdatesCallback(@NonNull LocationCallback callback) {
        locationCallback = callback;
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void getLastKnownLocation(@NonNull OnSuccessListener<Location> locationOnSuccessListener) {
        try {
            if (fusedLocationClient == null) {
                initLocationClient();
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(locationOnSuccessListener);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startRequestingLocationUpdates() {
        Log.i(GpsLocationManager.TAG, "Starting location updates");
        try {
            if (fusedLocationClient == null) {
                initLocationClient();
            }
            createLocationUpdatesRequest();
            if (fusedLocationClient != null && locationCallback != null) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void stopRequestingLocationUpdates() {
        Log.i(GpsLocationManager.TAG, "Stopping location updates");
        try {
            if (fusedLocationClient == null) {
                initLocationClient();
            }
            if (fusedLocationClient != null && locationCallback != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}

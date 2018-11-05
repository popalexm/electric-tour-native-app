package com.grandtour.ev.evgrandtour.app;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.DirectionsAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class Injection {

    /**
     * Provides the global Application Context, which is already a singleton, not an activity / fragment Context instance, not a memory leak .
     */
    @SuppressLint("StaticFieldLeak")
    @NonNull
    private static Context context;
    @NonNull
    private static final String SHARED_PREFERENCES = "app_status";
    @NonNull
    private static final String directionsBaseUrl = "https://maps.googleapis.com/maps/api/";
    @NonNull
    private static final String baseBackendUrl = "https://wenwere.com/";

    public static void initialize(@NonNull Context context) {
        Injection.context = context;
    }

    @NonNull
    public static Context provideGlobalContext() {
        return Injection.context;
    }

    @NonNull
    public static LocalStorageManager provideStorageManager() {
        return LocalStorageManager.getInstance(Injection.context);
    }

    @NonNull
    public static DirectionsAPI provideDirectionsApi() {return NetworkManager.getDirectionsAPIService(Injection.directionsBaseUrl);}

    @NonNull
    public static BackendAPI provideBackendApi() {return NetworkManager.getBackendAPIService(Injection.baseBackendUrl);}

    @NonNull
    public static SharedPreferences provideSharedPreferences () {return Injection.context.getSharedPreferences(Injection.SHARED_PREFERENCES, Context.MODE_PRIVATE);}

}

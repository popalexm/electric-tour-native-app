package com.grandtour.ev.evgrandtour.app;

import com.grandtour.ev.evgrandtour.BuildConfig;
import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.network.BackendAPI;
import com.grandtour.ev.evgrandtour.data.network.GoogleMapsAPI;
import com.grandtour.ev.evgrandtour.data.network.HolidayTripCloudAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkManager;
import com.grandtour.ev.evgrandtour.domain.schedulers.RxJavaSchedulers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.annotation.NonNull;

public final class Injection {

    /**
     * Provides the global Application Context, which is already a singleton, not an activity / fragment Context instance, not a memory leak .
     */
    @SuppressLint("StaticFieldLeak")
    @NonNull
    private static Context context;
    @NonNull
    private static final String SHARED_PREFERENCES = "app_status";
   /* @NonNull
    private static final String directionsBaseUrl = "https://maps.googleapis.com/maps/api/";
    @NonNull
    private static final String baseBackendUrl = "https://wenwere.com/";
    @NonNull
    private static final String baseCloudAPI = "http://192.168.100.2:8080/"; */

    private Injection() {
    }

    static void initialize(@NonNull Context context) {
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
    public static GoogleMapsAPI provideDirectionsApi() {
        return NetworkManager.getDirectionsAPIService(BuildConfig.DIRECTIONS_API_URL);
    }

    @NonNull
    public static BackendAPI provideBackendApi() {
        return NetworkManager.getBackendAPIService(BuildConfig.BACKED_API_URL);
    }

    @NonNull
    public static HolidayTripCloudAPI provideCloudApi() {
        return NetworkManager.getCloudAPIService(BuildConfig.CLOUD_API_URL);
    }

    @NonNull
    public static SharedPreferences provideSharedPreferences () {return Injection.context.getSharedPreferences(Injection.SHARED_PREFERENCES, Context.MODE_PRIVATE);}

    @NonNull
    public static Resources provideResources() {
        return Injection.context.getResources();
    }

    @NonNull
    public static RxJavaSchedulers provideRxSchedulers() {
        return RxJavaSchedulers.getRxJavaSchedulers();
    }

}

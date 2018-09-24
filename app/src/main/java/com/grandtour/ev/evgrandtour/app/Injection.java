package com.grandtour.ev.evgrandtour.app;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.network.NetworkAPI;
import com.grandtour.ev.evgrandtour.data.network.NetworkManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class Injection {

    /**
     * Provides the global application context, not a memory leak .
     */
    @NonNull
    private static Context context;
    @NonNull
    private static final String SHARED_PREFERENCES = "app_status";
    @NonNull
    private static final String baseUrl = "https://maps.googleapis.com/maps/api/";

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
    public static NetworkAPI provideNetworkApi() {return NetworkManager.getNetworkService(Injection.baseUrl);}

    @NonNull
    public static SharedPreferences provideSharedPreferences () {return Injection.context.getSharedPreferences(Injection.SHARED_PREFERENCES, Context.MODE_PRIVATE);}

}

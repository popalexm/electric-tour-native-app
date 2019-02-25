package com.grandtour.ev.evgrandtour.data.network;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkManager {

    private static GoogleMapsAPI googleMapsAPI;
    private static BackendAPI backendAPI;
    private static HolidayTripCloudAPI cloudAPI;

    private NetworkManager() { }

    @Nonnull
    public static GoogleMapsAPI getDirectionsAPIService(@NonNull String baseUrl) {
        if (NetworkManager.googleMapsAPI == null) {
             OkHttpClient okHttpClient = NetworkManager.buildOkHttpClient();
             Retrofit retrofit = NetworkManager.buildRetrofit(baseUrl, okHttpClient);
            NetworkManager.googleMapsAPI = retrofit.create(GoogleMapsAPI.class);
            return NetworkManager.googleMapsAPI;
        }
        return NetworkManager.googleMapsAPI;
    }

    //TODO Remove this when migration to new backend is done
    @Deprecated
    @Nonnull
    public static BackendAPI getBackendAPIService(@NonNull String baseUrl) {
        if (NetworkManager.backendAPI == null) {
            OkHttpClient okHttpClient = NetworkManager.buildOkHttpClient();
            Retrofit retrofit = NetworkManager.buildRetrofit(baseUrl, okHttpClient);
            NetworkManager.backendAPI = retrofit.create(BackendAPI.class);
            return NetworkManager.backendAPI;
        }
        return NetworkManager.backendAPI;
    }

    @Nonnull
    public static HolidayTripCloudAPI getCloudAPIService(@NonNull String baseUrl) {
        if (NetworkManager.cloudAPI == null) {
            OkHttpClient okHttpClient = NetworkManager.buildOkHttpClient();
            Retrofit retrofit = NetworkManager.buildRetrofit(baseUrl, okHttpClient);
            NetworkManager.cloudAPI = retrofit.create(HolidayTripCloudAPI.class);
            return NetworkManager.cloudAPI;
        }
        return NetworkManager.cloudAPI;
    }

    @NonNull
    private static Retrofit buildRetrofit(@NonNull String baseUrl , @NonNull OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @NonNull
    private static OkHttpClient buildOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder().addInterceptor(logging)
                .build();
    }
}

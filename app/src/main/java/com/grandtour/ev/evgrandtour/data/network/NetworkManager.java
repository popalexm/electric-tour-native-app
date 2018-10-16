package com.grandtour.ev.evgrandtour.data.network;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkManager {

    private static DirectionsAPI directionsAPI;
    private static BackendAPI backendAPI;

    private NetworkManager() { }

    @Nonnull
    public static DirectionsAPI getDirectionsAPIService(@NonNull String baseUrl) {
        if (NetworkManager.directionsAPI == null) {
             OkHttpClient okHttpClient = NetworkManager.buildOkHttpClient();
             Retrofit retrofit = NetworkManager.buildRetrofit(baseUrl, okHttpClient);
             NetworkManager.directionsAPI = retrofit.create(DirectionsAPI.class);
             return NetworkManager.directionsAPI;
        }
        return NetworkManager.directionsAPI;
    }

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
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(logging)
                .build();
    }
}

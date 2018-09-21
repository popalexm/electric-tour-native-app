package com.grandtour.ev.evgrandtour.data.network;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkManager {

    private static NetworkAPI networkAPI;

    private NetworkManager() { }

    @Nonnull
    public static NetworkAPI getNetworkService(@NonNull String baseUrl) {
        if (NetworkManager.networkAPI == null) {
             OkHttpClient okHttpClient = NetworkManager.buildOkHttpClient();
             Retrofit retrofit = NetworkManager.buildRetrofit(baseUrl, okHttpClient);
             NetworkManager.networkAPI = retrofit.create(NetworkAPI.class);
             return NetworkManager.networkAPI;
        }
        return NetworkManager.networkAPI;
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

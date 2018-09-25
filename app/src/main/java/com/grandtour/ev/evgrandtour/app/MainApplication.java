package com.grandtour.ev.evgrandtour.app;

import com.facebook.stetho.Stetho;

import android.app.Application;

import io.reactivex.plugins.RxJavaPlugins;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        Injection.initialize(this);
        RxJavaPlugins.setErrorHandler(e -> {
        });
    }
}

package com.grandtour.ev.evgrandtour.app;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import android.app.Application;

import io.fabric.sdk.android.Fabric;
import io.reactivex.plugins.RxJavaPlugins;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        Injection.initialize(this);
        RxJavaPlugins.setErrorHandler(e -> {
        });
    }
}

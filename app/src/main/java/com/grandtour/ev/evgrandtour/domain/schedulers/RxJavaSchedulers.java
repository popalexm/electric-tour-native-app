package com.grandtour.ev.evgrandtour.domain.schedulers;

import android.support.annotation.NonNull;
import android.util.Pair;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxJavaSchedulers {

    private static RxJavaSchedulers instance;

    @NonNull
    public static RxJavaSchedulers getRxJavaSchedulers() {
        if (RxJavaSchedulers.instance == null) {
            RxJavaSchedulers.instance = new RxJavaSchedulers();
        }
        return RxJavaSchedulers.instance;
    }

    /**
     * Returns a pair of RxJava schedulers, first for the execution thread which
     * is async on Schedulers.io() , second for the observer thread which runs on the main android thread (UI)
     */
    @NonNull
    public Pair<Scheduler, Scheduler> getDefault() {
        return new Pair<>(Schedulers.io(), AndroidSchedulers.mainThread());
    }
}

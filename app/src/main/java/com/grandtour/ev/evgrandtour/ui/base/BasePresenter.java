package com.grandtour.ev.evgrandtour.ui.base;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePresenter implements BaseContract.Presenter {

    @NonNull
    private final CompositeDisposable subscribers = new CompositeDisposable();
    protected boolean isViewAttached;

    protected void addSubscription(@NonNull Disposable subscriber) {
        this.subscribers.add(subscriber);
    }

    private void removeSubscriptions() {
        subscribers.clear();
    }

    @Override
    public void onAttach() {
        isViewAttached = true;
    }

    @Override
    public void onDetach() {
        isViewAttached = false;
    }

    @Override
    public void onDestroy() {
        removeSubscriptions();
    }
}

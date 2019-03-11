package com.grandtour.ev.evgrandtour.ui.base;

import androidx.annotation.NonNull;
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
    public void onAttachView() {
        isViewAttached = true;
    }

    @Override
    public void onDetachView() {
        isViewAttached = false;
    }

    @Override
    public void onDestroyView() {
        removeSubscriptions();
    }
}

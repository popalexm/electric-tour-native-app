package com.grandtour.ev.evgrandtour.domain.base;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public interface BaseUseCaseFlowable<T> {

    Flowable<T> perform();
}

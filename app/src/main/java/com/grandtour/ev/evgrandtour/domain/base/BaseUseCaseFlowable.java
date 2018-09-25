package com.grandtour.ev.evgrandtour.domain.base;

import io.reactivex.Flowable;

public interface BaseUseCaseFlowable<T> {

    Flowable<T> perform();
}

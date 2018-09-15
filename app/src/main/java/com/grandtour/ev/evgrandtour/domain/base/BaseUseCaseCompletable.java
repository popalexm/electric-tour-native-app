package com.grandtour.ev.evgrandtour.domain.base;

import io.reactivex.Completable;

public interface BaseUseCaseCompletable<T> {

    Completable perform();
}

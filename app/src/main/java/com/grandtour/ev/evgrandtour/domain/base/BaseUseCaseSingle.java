package com.grandtour.ev.evgrandtour.domain.base;

import io.reactivex.Single;

public interface BaseUseCaseSingle<T> {

    Single<T> perform();
}

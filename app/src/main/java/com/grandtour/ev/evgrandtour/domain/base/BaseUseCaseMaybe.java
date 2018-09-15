package com.grandtour.ev.evgrandtour.domain.base;

import io.reactivex.Maybe;

public interface BaseUseCaseMaybe<T> {

    Maybe<T> perform();

}

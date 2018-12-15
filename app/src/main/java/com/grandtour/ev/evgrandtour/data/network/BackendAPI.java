package com.grandtour.ev.evgrandtour.data.network;

import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.AvailableToursResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface BackendAPI {

    @GET("tours/")
    Maybe<Response<List<AvailableToursResponse>>> getAllTours(@Header("X-XSRF-TOKEN") String authToken);

    @GET("tours/{id}")
    Maybe<Response<TourDataResponse>> getTourById(@Header("X-XSRF-TOKEN") String authToken, @Path("id") String tourId);

    @GET("users/is-authorized")
    Completable validateUserToken(@Header("X-XSRF-TOKEN") String authToken);
}

package com.grandtour.ev.evgrandtour.data.network;
import com.grandtour.ev.evgrandtour.data.network.models.response.entireTour.EntireTourResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.tour.TourDataResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.tour.TourResponse;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BackendAPI {

    @GET("entire-tour/")
    Maybe<Response<EntireTourResponse>> getEntireTourCheckpoints();

    @GET("tours/")
    Maybe<Response<List<TourResponse>>> getAllTours();

    @GET("tours/")
    Maybe<Response<TourDataResponse>> getTourById(@Path("id") String tourId);
}

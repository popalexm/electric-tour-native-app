package com.grandtour.ev.evgrandtour.data.network;
import com.grandtour.ev.evgrandtour.data.network.models.response.entireTour.EntireTourResponse;

import io.reactivex.Maybe;
import retrofit2.Response;
import retrofit2.http.GET;

public interface BackendAPI {

    @GET("entire-tour/")
    Maybe<Response<EntireTourResponse>> getEntireTourCheckpoints();
}

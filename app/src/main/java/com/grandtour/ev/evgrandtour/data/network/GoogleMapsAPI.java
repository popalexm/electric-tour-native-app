package com.grandtour.ev.evgrandtour.data.network;

import com.grandtour.ev.evgrandtour.data.network.models.response.elevation.ElevationResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;

import io.reactivex.Maybe;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsAPI {

    @GET("directions/json")
    Maybe<Response<RoutesResponse>> getDirectionsForWaypoints(@Query("origin") String origin, @Query("destination") String destination, @Query("waypoints") String transitWaypoints, @Query("key") String apiKey);

    @GET("elevation/json")
    Maybe<Response<ElevationResponse>> getElevationForCheckpoints(@Query("locations") String checkpoints, @Query("key") String apiKey);

}

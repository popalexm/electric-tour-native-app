package com.grandtour.ev.evgrandtour.data.network;

import com.grandtour.ev.evgrandtour.data.network.models.response.distance.DistanceResponse;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;

import io.reactivex.Maybe;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkAPI {

    @GET("directions/json?")
    Maybe<Response<RoutesResponse>> getDirectionsForWaypoints(@Query("origin") String origin, @Query("destination") String destination, @Query("waypoints") String transitWaypoints, @Query("key") String apiKey);

    @GET("distancematrix/json?")
    Maybe<Response<DistanceResponse>> getDistanceBetweenPoints(@Query("origins") String origin, @Query("destinations") String destination,
            @Query("key") String apiKey);

}

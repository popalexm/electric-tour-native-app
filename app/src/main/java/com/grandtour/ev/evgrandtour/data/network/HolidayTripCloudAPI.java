package com.grandtour.ev.evgrandtour.data.network;

import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedTripRequest;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface HolidayTripCloudAPI {

    @POST("post-new-planned-trip-for-user-id")
    Observable<Response<String>> postPlannedTripToCloud(@Header("userId") Integer userId, @Body PlannedTripRequest plannedTripRequest);

}

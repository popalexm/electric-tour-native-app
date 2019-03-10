package com.grandtour.ev.evgrandtour.data.network;

import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedCheckpointRequest;
import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedTripRequest;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningTripResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HolidayTripCloudAPI {

    @POST("post-new-planned-trip-for-user-id")
    Observable<Response<String>> postPlannedTripToCloud(@Header("userId") Integer userId, @Body PlannedTripRequest plannedTripRequest);

    @GET("get-current-in-planning-trip")
    Observable<Response<InPlanningTripResponse>> getCurrentInPlanningTripForUser(@Query("userId") Integer userId);

    @POST("post-in-planning-checkpoint-for-trip")
    Observable<Response<Integer>> postPlannedCheckpointForTripId(@Query("tripId") Integer tripId, @Query("userId") Integer userId,
            @Body PlannedCheckpointRequest plannedCheckpointRequest);

    @DELETE("/delete-in-planning-checkpoint-for-trip-id")
    Observable<Response<Integer>> deletePlannedCheckpointForTripId(@Query("tripId") Integer tripId, @Query("checkpointId") Integer checkpointId);

}

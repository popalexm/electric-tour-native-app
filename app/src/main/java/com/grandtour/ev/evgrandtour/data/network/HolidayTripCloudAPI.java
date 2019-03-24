package com.grandtour.ev.evgrandtour.data.network;

import com.grandtour.ev.evgrandtour.data.network.models.request.PlannedCheckpointRequest;
import com.grandtour.ev.evgrandtour.data.network.models.request.AddInPlanningTripRequest;
import com.grandtour.ev.evgrandtour.data.network.models.request.UpdateCheckpointLocationRequest;
import com.grandtour.ev.evgrandtour.data.network.models.response.planNewTrip.InPlanningTripResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HolidayTripCloudAPI {

    @POST("post-in-planning-trip")
    Observable<Response<Integer>> postPlannedTrip(@Body AddInPlanningTripRequest addInPlanningTripRequest);

    @GET("get-current-in-planning-trip")
    Observable<Response<InPlanningTripResponse>> getCurrentInPlanningTripForUser(@Query("userId") Integer userId);

    @POST("post-in-planning-checkpoint")
    Observable<Response<Integer>> postPlannedCheckpoint(@Body PlannedCheckpointRequest plannedCheckpointRequest);

    @DELETE("delete-in-planning-checkpoint-for-trip-id")
    Observable<Response<Integer>> deletePlannedCheckpointForTripId(@Query("tripId") Integer tripId, @Query("checkpointId") Integer checkpointId);

    @PATCH("update-in-planning-checkpoint-location")
    Observable<Response<Integer>> updatePlannedCheckpointLocationForTripId(@Query("userId") Integer userId, @Body UpdateCheckpointLocationRequest request);

    @POST("set-in-planning-trip-as-done")
    Observable<Response<Integer>> setInPlanningTripAsDone(@Body Integer userId);
}

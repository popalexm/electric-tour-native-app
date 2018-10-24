package com.grandtour.ev.evgrandtour.domain.handlers;

import com.grandtour.ev.evgrandtour.data.database.LocalStorageManager;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Leg;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.Route;
import com.grandtour.ev.evgrandtour.data.network.models.response.routes.RoutesResponse;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public final class RoutesResponseHandler {

    @NonNull
    private static final String TAG = RoutesResponseHandler.class.getSimpleName();
    @NonNull
    private final static String OK = "OK";
    @NonNull
    private final static String ZERO_RESULTS = "ZERO_RESULTS";
    @NonNull
    private final static String INVALID_REQUEST = "INVALID_REQUEST";
    @NonNull
    private final static String OVER_DAILY_LIMIT = "OVER_DAILY_LIMIT";
    @NonNull
    private final static String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";

    @NonNull
    private final LocalStorageManager storageManager;

    public RoutesResponseHandler(@NonNull LocalStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void handleRouteResponse(@NonNull RoutesResponse responseBody, List<Checkpoint> singleRouteRequestBatch) {
        if (isRequestDataOk(responseBody.getStatus())) {
            List<Route> routes = responseBody.getRoutes();
            if (routes != null && routes.size() != 0) {
                Route responseRoute = responseBody.getRoutes()
                        .get(0);
                if (responseRoute != null) {
                    List<Leg> routeLegs = responseRoute.getLegs();
                    if (routeLegs != null) {
                        for (int legIndex = 0; legIndex < routeLegs.size(); legIndex++) {
                            int checkpointId = singleRouteRequestBatch.get(legIndex)
                                    .getCheckpointId();
                            int distanceToNext = routeLegs.get(legIndex)
                                    .getDistance()
                                    .getValue();
                            int durationToNext = routeLegs.get(legIndex)
                                    .getDuration()
                                    .getValue();
                            storageManager.checkpointsDao()
                                    .updateCheckpointById(checkpointId, distanceToNext, durationToNext);
                        }
                    }
                }
            }
        }
    }

    private boolean isRequestDataOk(@NonNull String statusCode) {
        switch (statusCode) {
            case RoutesResponseHandler.OK:
                return true;
            case RoutesResponseHandler.ZERO_RESULTS:
                Log.e(RoutesResponseHandler.TAG, "Response on directions API contains zero results");
                return false;
            case RoutesResponseHandler.INVALID_REQUEST:
                Log.e(RoutesResponseHandler.TAG, "Response on directions API returned invalid request");
                return false;
            case RoutesResponseHandler.OVER_DAILY_LIMIT:
                Log.e(RoutesResponseHandler.TAG, "Response on directions API returned over daily limit");
                return false;
            case RoutesResponseHandler.OVER_QUERY_LIMIT:
                Log.e(RoutesResponseHandler.TAG, "Response on directions API returned query limit exceeded");
                return false;
        }
        return false;
    }
}

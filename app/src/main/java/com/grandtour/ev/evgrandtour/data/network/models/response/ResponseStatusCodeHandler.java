package com.grandtour.ev.evgrandtour.data.network.models.response;

import android.support.annotation.NonNull;
import android.util.Log;

public final class ResponseStatusCodeHandler {

    @NonNull
    private static final String TAG = ResponseStatusCodeHandler.class.getSimpleName();

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

    private ResponseStatusCodeHandler() {
    }

    public boolean isRequestCodeOK(@NonNull String statusCode) {
        switch (statusCode) {
            case ResponseStatusCodeHandler.OK:
                return true;
            case ResponseStatusCodeHandler.ZERO_RESULTS:
                Log.e(TAG, "Response on directions API contains zero results");
                return false;
        }
        return false;
    }

}

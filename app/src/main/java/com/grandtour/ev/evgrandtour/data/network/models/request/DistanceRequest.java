package com.grandtour.ev.evgrandtour.data.network.models.request;

import com.google.android.gms.maps.model.LatLng;

import android.support.annotation.NonNull;

public class DistanceRequest {

    @NonNull
    public String startpoint;
    @NonNull
    public String endWaypoint;
    @NonNull
    public String apiKey;

    DistanceRequest(@NonNull String startWaypoint, @NonNull String endWaypoint, @NonNull String apiKey) {
        this.startpoint = startWaypoint;
        this.endWaypoint = endWaypoint;
        this.apiKey = apiKey;
    }

    public static class DistanceRequestBuilder {

        private String startWaypoint;
        private String endWaypoint;
        private String apiKey;

        public DistanceRequestBuilder setStartWaypoint(@NonNull LatLng startLatLang) {
            this.startWaypoint = startLatLang.latitude + "," + startLatLang.longitude;
            return this;
        }

        public DistanceRequestBuilder setEndWaypoint(@NonNull LatLng endLatLng) {
            this.endWaypoint = endLatLng.latitude + "," + endLatLng.longitude;
            return this;
        }

        public DistanceRequestBuilder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public DistanceRequest createDistanceRequest() {
            return new DistanceRequest(startWaypoint, endWaypoint, apiKey);
        }
    }
}

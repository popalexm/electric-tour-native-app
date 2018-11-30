package com.grandtour.ev.evgrandtour.data.network.models.request;

import com.google.android.gms.maps.model.LatLng;

import android.support.annotation.NonNull;

import java.util.List;

public class RouteDirectionsRequest {

    @NonNull
    public String startWaypoint;
    @NonNull
    public String endWaypoint;
    @NonNull
    public String transitWaypoints;
    @NonNull
    public String mode;
    @NonNull
    public String apiKey;

    RouteDirectionsRequest(@NonNull String startWaypoint, @NonNull String endWaypoint, @NonNull String transitWaypoints, @NonNull String mode,
            @NonNull String apiKey) {
        this.startWaypoint = startWaypoint;
        this.endWaypoint = endWaypoint;
        this.transitWaypoints = transitWaypoints;
        this.mode = mode;
        this.apiKey = apiKey;
    }

    public static class RouteParametersBuilder {

        private String startWaypoint;
        private String endWaypoint;
        private String mode;
        private String apiKey;
        private String transitWaypoints;

        public RouteParametersBuilder setStartWaypoint(@NonNull LatLng startLatLang) {
            this.startWaypoint = startLatLang.latitude + "," + startLatLang.longitude;
            return this;
        }

        public RouteParametersBuilder setEndWaypoint(@NonNull LatLng endLatLng ) {
            this.endWaypoint =  endLatLng.latitude + "," + endLatLng.longitude;
            return this;
        }

        public RouteParametersBuilder setTransitWaypoints(@NonNull List<LatLng> transitWaypoints){
            if (this.transitWaypoints == null) {
                this.transitWaypoints = "";
            }
            for (int index = 0; index < transitWaypoints.size(); index++) {
                LatLng waypoint = transitWaypoints.get(index);
                if (index == 0) {
                    this.transitWaypoints = this.transitWaypoints + waypoint.latitude + "," + waypoint.longitude;
                } else {
                    this.transitWaypoints = this.transitWaypoints + "|" + waypoint.latitude + "," + waypoint.longitude;
                }
            }
            return this;
        }

        public RouteParametersBuilder setMode(@NonNull String mode) {
            this.mode = mode;
            return this;
        }

        public RouteParametersBuilder setAPIKey(@NonNull String apiKey){
            this.apiKey = apiKey;
            return this;
        }

        public RouteDirectionsRequest createRouteParameters() {
            return new RouteDirectionsRequest(startWaypoint, endWaypoint, transitWaypoints, mode, apiKey);
        }
    }
}

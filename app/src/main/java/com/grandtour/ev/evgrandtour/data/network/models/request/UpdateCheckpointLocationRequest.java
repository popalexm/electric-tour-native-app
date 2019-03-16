package com.grandtour.ev.evgrandtour.data.network.models.request;

import androidx.annotation.NonNull;

public class UpdateCheckpointLocationRequest {

    @NonNull
    private Integer checkpointId;
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;

    public UpdateCheckpointLocationRequest(@NonNull Integer checkpointId, @NonNull Double latitude, @NonNull Double longitude) {
        this.checkpointId = checkpointId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

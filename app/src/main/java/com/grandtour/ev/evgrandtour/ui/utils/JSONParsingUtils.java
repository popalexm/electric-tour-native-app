package com.grandtour.ev.evgrandtour.ui.utils;

import com.google.gson.JsonParseException;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.entireTour.ImportCheckpoint;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class JSONParsingUtils {

    private JSONParsingUtils() {
    }

    @NonNull
    public static List<Checkpoint> processImportedCheckpoints(@NonNull List<ImportCheckpoint> checkPointsFromJson) {
        List<Checkpoint> toSaveCheckpoints = new ArrayList<>();
        for (ImportCheckpoint importCheckpoint : checkPointsFromJson) {
            try {
                String lat = importCheckpoint.getLatitude();
                String lng = importCheckpoint.getLongitude();
                if (lat != null && lng != null){
                    Checkpoint checkpoint = new Checkpoint();
                    checkpoint.setCheckpointId(importCheckpoint.getCheckpointId());
                    checkpoint.setCheckpointName(importCheckpoint.getCheckpointName());
                    checkpoint.setLatitude(Double.parseDouble(lat));
                    checkpoint.setLongitude(Double.parseDouble(lng));
                    toSaveCheckpoints.add(checkpoint);
                }
            } catch (NumberFormatException | JsonParseException e) {
                e.printStackTrace();
            }
        }
        return toSaveCheckpoints;
    }
}

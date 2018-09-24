package com.grandtour.ev.evgrandtour.ui.utils;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.ui.maps.models.ImportCheckpoint;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JSONParsingUtils {

    private static final Pattern WILDCARD = Pattern.compile(",", Pattern.LITERAL);

    private JSONParsingUtils() {
    }

    @NonNull
    private static Double filterLatLngValues(@NonNull CharSequence number) {
        String corrected = JSONParsingUtils.WILDCARD.matcher(number)
                    .replaceAll(Matcher.quoteReplacement(""));
        String start = corrected.substring(0, 2);
        String end = corrected.substring(2);
        return Double.valueOf(start + "." + end);
    }

    @NonNull
    public static List<Checkpoint> processImportedCheckpoints(@NonNull List<ImportCheckpoint> checkPointsFromJson) {
        List<Checkpoint> toSaveCheckpoints = new ArrayList<>();
        for (ImportCheckpoint importCheckpoint : checkPointsFromJson) {
            try {
                double lat = JSONParsingUtils.filterLatLngValues(importCheckpoint.getLatitude());
                double lng = JSONParsingUtils.filterLatLngValues(importCheckpoint.getLongitude());
                Checkpoint checkpoint = new Checkpoint();
                checkpoint.setCheckpointId(importCheckpoint.getCheckpointId());
                checkpoint.setCheckpointName(importCheckpoint.getCheckpointName());
                checkpoint.setLatitude(lat);
                checkpoint.setLongitude(lng);
                toSaveCheckpoints.add(checkpoint);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return toSaveCheckpoints;
    }
}

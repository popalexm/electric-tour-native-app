package com.grandtour.ev.evgrandtour.ui.utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public final class ArrayUtils {

    private ArrayUtils() { }

    /**
     * Utility method that splits and array of checkpoints into smaller batches,
     * Last element of an batch is put as at the first element of the next,
     * see maxCheckpoints - 1 at the end of the for operator.
     */
    @NonNull
    public static <T> List<List<T>> splitCheckpointsIntoBatches(List<T> checkpointsList, int maxCheckpointBatchSize) {
        List<List<T>> checkpointsBatch = new ArrayList<>();
        final int checkpointListSize = checkpointsList.size();
        for (int i = 0; i < checkpointListSize; i += maxCheckpointBatchSize - 1) {
            checkpointsBatch.add(new ArrayList<T>(checkpointsList.subList(i, Math.min(checkpointListSize, i + maxCheckpointBatchSize))));
        }
        return checkpointsBatch;
    }
}

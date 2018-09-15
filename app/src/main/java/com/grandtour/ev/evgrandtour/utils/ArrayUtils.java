package com.grandtour.ev.evgrandtour.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class ArrayUtils {

    private ArrayUtils() { }

    @NonNull
    public static <T> List<List<T>> split(List<T> list, int maxArrayLength) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += maxArrayLength) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + maxArrayLength)))
            );
        }
        return parts;
    }
}

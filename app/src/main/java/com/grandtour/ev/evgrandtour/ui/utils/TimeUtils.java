package com.grandtour.ev.evgrandtour.ui.utils;

import androidx.annotation.NonNull;

public final class TimeUtils {

    private TimeUtils() {
    }

    @NonNull
    public static String convertFromSecondsToFormattedTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        return String.format("%02dh : %02d min", hours, minutes);
    }

}

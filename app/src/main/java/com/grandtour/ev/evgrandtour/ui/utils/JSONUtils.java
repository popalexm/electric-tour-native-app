package com.grandtour.ev.evgrandtour.ui.utils;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JSONUtils {

    private static final Pattern WILDCARD = Pattern.compile(",", Pattern.LITERAL);

    private JSONUtils() { }

    @NonNull
    public static Double filterLatLngValues(@NonNull CharSequence number) {
        String corrected = JSONUtils.WILDCARD.matcher(number)
                    .replaceAll(Matcher.quoteReplacement(""));
        String start = corrected.substring(0, 2);
        String end = corrected.substring(2);
        return Double.valueOf(start + "." + end);
    }
}

package com.grandtour.ev.evgrandtour.ui.mapsView.settingsDialog;

import android.support.annotation.NonNull;

public final class SharedPreferencesKeys {

    @NonNull
    public final static String KEY_LOCATION_TRACKING_ENABLED = "areLocationUpdatesEnabled";
    @NonNull
    public final static String KEY_ROUTE_DEVIATION_NOTIFICATIONS_ENABLED = "areDeviationNotificationsEnabled";

    private SharedPreferencesKeys() {
    }
}

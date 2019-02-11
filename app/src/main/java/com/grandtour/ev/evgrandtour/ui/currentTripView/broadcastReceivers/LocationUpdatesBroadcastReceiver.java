package com.grandtour.ev.evgrandtour.ui.currentTripView.broadcastReceivers;

import com.grandtour.ev.evgrandtour.ui.currentTripView.CurrentTripFragmentContract;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {

    @NonNull
    public static final String LOCATION_REQUESTS_BUNDLE = "CurrentLocationBundle";

    @NonNull
    private final CurrentTripFragmentContract.Presenter presenter;

    public LocationUpdatesBroadcastReceiver(@NonNull CurrentTripFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundleContent = intent.getExtras();
            if (bundleContent != null) {
                for (String keySet : bundleContent.keySet()) {
                    switch (keySet) {
                        case LocationUpdatesBroadcastReceiver.LOCATION_REQUESTS_BUNDLE:
                            Location currentLocation = bundleContent.getParcelable(LocationUpdatesBroadcastReceiver.LOCATION_REQUESTS_BUNDLE);
                            if (currentLocation != null) {
                                presenter.onCurrentLocationChanged(currentLocation);
                            }
                            break;
                    }
                }
            }
        }
    }
}

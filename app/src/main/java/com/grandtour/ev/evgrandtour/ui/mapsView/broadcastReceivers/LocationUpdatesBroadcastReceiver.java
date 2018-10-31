package com.grandtour.ev.evgrandtour.ui.mapsView.broadcastReceivers;

import com.grandtour.ev.evgrandtour.services.LocationsUpdatesService;
import com.grandtour.ev.evgrandtour.ui.mapsView.MapsFragmentContract;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {

    @NonNull
    private final MapsFragmentContract.Presenter presenter;

    public LocationUpdatesBroadcastReceiver(@NonNull MapsFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundleContent = intent.getExtras();
            if (bundleContent != null) {
                for (String keySet : bundleContent.keySet()) {
                    switch (keySet) {
                        case LocationsUpdatesService.LOCATION_REQUESTS_BUNDLE:
                            Location currentLocation = bundleContent.getParcelable(LocationsUpdatesService.LOCATION_REQUESTS_BUNDLE);
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

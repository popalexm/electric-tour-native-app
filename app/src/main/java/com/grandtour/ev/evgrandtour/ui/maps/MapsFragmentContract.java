package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

public class MapsFragmentContract {

    public interface View extends BaseContract.View{

        void updateCurrentUserLocation(@NonNull LatLng latLng);

        void loadDestinations(@NonNull List<MarkerOptions> destinations);

        void openFileExplorer();

        void clearMapWaypoints();

        void clearMapRoutes();

        void drawWaypointRoute(@NonNull PolylineOptions routePolyOptions);

    }

    public interface Presenter extends BaseContract.Presenter {

        void onMapReady();

        void onStartLocationService(@NonNull Context context);

        void onStopLocationService(@NonNull Context context);

        void onCurrentLocationChanged(@NonNull Location coordinates);

        void onLocalFileOpened(@NonNull Uri fileUri);

        void onClearWaypointsClicked();

        void onCalculateRoutesClicked(@NonNull List<Marker> waypoints);

        void onStopCalculatingRoutesClicked();
    }
}

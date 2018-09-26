package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentContract {

    public interface View extends BaseContract.View{

        void updateCurrentUserLocation(@NonNull LatLng latLng);

        void loadCheckpoints(@NonNull List<Pair<Integer, MarkerOptions>> checkpoints);

        void openFileExplorer();

        void clearMapCheckpoints();

        void clearMapRoutes();

        void drawCheckpointsRoute(@NonNull PolylineOptions routePolyOptions);

        void showTotalRouteLength(int length);

        void showRouteReCalculationsDialog();
    }

    public interface Presenter extends BaseContract.Presenter {

        void onMapReady();

        void onUnBindDirectionsRequestService();

        void onCalculatingRoutesStarted();

        void onCalculatingRoutesDone();

        void onRoutesRequestsError(@NonNull String errorType);

        void onCurrentLocationChanged(@NonNull Location coordinates);

        void onLocalFileOpened(@NonNull Uri fileUri);

        void onClearCheckpointsAndRoutesClicked();

        void onCalculateRoutesClicked();

        void onRecalculateRoutesConfirmation();

        void onStopCalculatingRoutesClicked();

        void onTotalRouteInfoClicked();

        void onNewRoutesReceived(@NonNull ArrayList<LatLng> routeMapPoints);
    }
}

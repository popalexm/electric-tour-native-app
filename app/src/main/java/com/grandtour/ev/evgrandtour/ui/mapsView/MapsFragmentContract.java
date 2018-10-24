package com.grandtour.ev.evgrandtour.ui.mapsView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.database.models.Tour;
import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentContract {

    public interface View extends BaseContract.View{

        void updateCurrentUserLocation(@NonNull LatLng latLng);

        void loadCheckpoints(@NonNull List<Pair<Integer, MarkerOptions>> checkpoints);

        void clearMapCheckpoints();

        void clearMapRoutes();

        void drawCheckpointsRoute(@NonNull PolylineOptions routePolyOptions);

        void showTotalRouteInformation(@NonNull String infoMessage, boolean shouldShowInfoCard);

        void startGoogleMapsDirections(@NonNull String navigationUri);

        void showCalculateDistanceDialog(@NonNull List<Checkpoint> checkpoints);

        void showTourPickerDialog(@NonNull List<Tour> tours);
    }

    public interface Presenter extends BaseContract.Presenter {

        void onMapReady();

        void onUnBindDirectionsRequestService();

        void onCalculatingRoutesStarted();

        void onCalculatingRoutesDone();

        void onRoutesRequestsError(@NonNull String errorType);

        void onCurrentLocationChanged(@NonNull Location coordinates);

        void onClearCheckpointsAndRoutesClicked();

        void onStopCalculatingRoutesClicked();

        void onNavigationClicked(@NonNull Marker originMarker);

        void onNewRoutesReceived(@NonNull ArrayList<LatLng> routeMapPoints);

        void onCalculateDistanceBetweenTwoCheckpointsClicked();

        void onChooseTourClicked();

        void onTourSelected(@NonNull String tourId);
    }
}

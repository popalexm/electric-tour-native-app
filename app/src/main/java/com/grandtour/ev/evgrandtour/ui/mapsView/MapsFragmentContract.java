package com.grandtour.ev.evgrandtour.ui.mapsView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.mapsView.search.SearchResultViewModel;
import com.grandtour.ev.evgrandtour.ui.mapsView.search.SearchViewResultClickListener;
import com.grandtour.ev.evgrandtour.ui.mapsView.settingsDialog.UpdateSettingsListener;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentContract {

    public interface View extends BaseContract.View, GoogleMap.OnInfoWindowLongClickListener, UpdateSettingsListener {

        void updateCurrentUserLocation(@NonNull LatLng latLng);

        void loadCheckpoints(@NonNull List<Pair<Integer, MarkerOptions>> checkpoints);

        void centerMapToCurrentSelectedRoute();

        void moveToMarker(@NonNull Integer markerCheckpointId);

        void clearMapCheckpoints();

        void clearMapRoutes();

        void drawCheckpointsRoute(@NonNull PolylineOptions routePolyOptions);

        void showTotalRouteInformation(@NonNull String infoMessage, boolean shouldShowInfoCard);

        void startGoogleMapsDirections(@NonNull String navigationUri);

        void showCalculateDistanceDialog(@NonNull List<Checkpoint> checkpoints);

        void showSettingsDialog();

        void showTourPickerDialog(@NonNull List<TourDataResponse> tours);

        void displaySearchResults(@NonNull List<SearchResultViewModel> checkpoints);

        void clearSearchResults();

        void hideSoftKeyboard();
    }

    public interface Presenter extends BaseContract.Presenter, SearchViewResultClickListener {

        void onMapReady();

        void onUnBindDirectionsRequestService();

        void onCalculatingRoutesStarted();

        void onCalculatingRoutesDone();

        void onRoutesRequestsError(@NonNull String errorType);

        void onCurrentLocationChanged(@NonNull Location coordinates);

        void onClearCheckpointsAndRoutesClicked();

        void onNavigationClicked(@NonNull Marker originMarker);

        void onNewRoutesReceived(@NonNull ArrayList<LatLng> routeMapPoints);

        void onCalculateDistanceBetweenTwoCheckpointsClicked();

        void onChooseTourClicked();

        void onTourSelected(@NonNull String tourId);

        void onNewSearchQuery(@NonNull String text);

        void onSearchQueryCleared();

        void onSettingsClicked();

        void onLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled);

        void onRouteDeviationTrackingUpdate(boolean isDeviationTrackingEnabled);

    }
}

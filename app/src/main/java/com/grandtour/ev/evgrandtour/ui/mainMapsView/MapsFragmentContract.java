package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.search.SearchResultViewModel;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.search.SearchViewResultClickListener;
import com.grandtour.ev.evgrandtour.ui.settings.UpdateSettingsListener;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.List;

public class MapsFragmentContract {

    public interface View extends BaseContract.View, UpdateSettingsListener, SelectedTourListener {

        void updateCurrentUserLocation(@NonNull LatLng latLng);

        void loadCheckpoints(@NonNull List<MapCheckpoint> checkpoints);

        void centerMapToCurrentSelectedRoute();

        void moveToMarker(@NonNull Integer markerCheckpointId);

        void clearMapCheckpoints();

        void clearMapRoutes();

        void drawRouteStepLineOnMap(@NonNull PolylineOptions routePolyOptions, int routeStepId);

        void showTotalRouteInformation(@NonNull String routeTitle, @NonNull String routeInfo);

        void startGoogleMapsDirections(@NonNull String navigationUri);

        void showCalculateDistanceDialog(@NonNull List<Checkpoint> checkpoints);

        void showSettingsDialog();

        void showTourPickerDialog();

        void displaySearchResults(@NonNull List<SearchResultViewModel> checkpoints);

        void clearSearchResults();

        void hideSoftKeyboard();

        void animateRouteSelectionButton();

        void animateRouteInformationText();

        void showElevationChartForRouteLegDialog(@NonNull Integer routeLegId);

        void showEntireRouteElevationChartDialog();

        void showChartView(@NonNull LineData lineData, @NonNull Description description);
    }

    public interface Presenter extends BaseContract.Presenter, SearchViewResultClickListener {

        void onMapReady();

        void onUnBindDirectionsRequestService();

        void onCalculatingRoutesStarted();

        void onCalculatingRoutesDone();

        void onRoutesRequestsError(@NonNull String errorType);

        void onCurrentLocationChanged(@NonNull Location coordinates);

        void onNavigationClicked(@NonNull MapCheckpoint originMarker);

        void onCalculateDistanceBetweenTwoCheckpointsClicked();

        void onChooseTourClicked();

        void onTourSelected(@NonNull String tourId, @NonNull List<TourDataResponse> responses);

        void onNewSearchQuery(@NonNull String text);

        void onSearchQueryCleared();

        void onSettingsClicked();

        void onRouteElevationChartClicked();

        void onLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled);

        void onPolylineClicked(Integer routeLegId);
    }
}

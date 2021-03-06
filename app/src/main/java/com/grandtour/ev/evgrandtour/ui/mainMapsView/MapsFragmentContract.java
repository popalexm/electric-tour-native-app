package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.listeners.OnQueryTextChangeListener;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.listeners.OnSearchResultClickListener;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.listeners.OnSearchViewCloseListener;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.listeners.OnSelectedTourListener;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.SearchResultModel;
import com.grandtour.ev.evgrandtour.ui.settings.UpdateSettingsListener;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.List;

public class MapsFragmentContract {

    public interface View extends BaseContract.View, UpdateSettingsListener, OnSelectedTourListener {

        void updateCurrentUserLocation(@NonNull LatLng latLng);

        void loadCheckpointsOnMapView(@NonNull List<MapCheckpoint> checkpoints);

        void centerMapToCurrentSelectedRoute(@NonNull List<MapCheckpoint> checkpoints);

        void clearMapCheckpoints();

        void clearMapRoutes();

        void drawRouteStepLineOnMap(@NonNull PolylineOptions routePolyOptions, int routeStepId);

        void showTotalRouteInformation(@NonNull String routeTitle, @NonNull String routeDrivingDistance, @NonNull String routeDrivingDuration);

        void startGoogleMapsDirections(@NonNull String navigationUri);

        void showSettingsDialog();

        void showTourPickerDialog();

        void displaySearchResults(@NonNull List<SearchResultModel> checkpoints);

        void clearSearchResults();

        void searchViewClosed();

        void searchViewOpen();

        void hideSoftKeyboard();

        void animateRouteSelectionButton();

        void animateRouteInformationText();

        void showChartView(@NonNull LineData lineData, @NonNull Description description);

        void loadAvailableFilterPoints(List<MapCheckpoint> availableFilterPoints);

        void showFilteringOptionsView();

        void clearFilteringChipsSelectionStatus();

        void moveCameraToCurrentLocation(@NonNull LatLng location);

        void showSelectTripButton(boolean shouldDisplaySelectTripButton);

        void showNavigationButton(boolean shouldDisplayNavigationLayout);

        void highLightNavigationPath(List<Integer> routeLegsIdsToHighLight);

        void clearAllHighlightedPaths();
    }

    public interface Presenter extends BaseContract.Presenter, OnSearchResultClickListener, OnQueryTextChangeListener, OnSearchViewCloseListener,
            android.view.View.OnClickListener {

        void onMapReady();

        void onUnBindDirectionsRequestService();

        void onCalculatingRoutesStarted();

        void onCalculatingRoutesDone();

        void onRoutesRequestsError(@NonNull String errorType);

        void onCurrentLocationChanged(@NonNull Location coordinates);

        void onNavigationClicked();

        void onChooseTourClicked();

        void onTourSelected(@NonNull String tourId, @NonNull List<TourDataResponse> responses);

        void onSettingsClicked();

        void onFilterButtonClicked();

        void onLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled);

        void onSelectedCheckpointRouteFilters(@NonNull List<MapCheckpoint> toFilterRouteByCheckpoints);

        void onClearFilteredRouteClicked();

        void onFilterChipSelectionRemoved();

        void onMyLocationButtonClicked();

        void onMarkerClicked(int checkpointId);

        void onMarkerInfoWindowClosed();

    }
}

package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.databinding.FragmentMainMapViewBinding;
import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.base.BaseFragment;
import com.grandtour.ev.evgrandtour.ui.chooseTour.ChooseTourDialogFragment;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.broadcastReceivers.LocationUpdatesBroadcastReceiver;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.broadcastReceivers.RouteRequestsBroadcastReceiver;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.chartView.XAxisValueFormatter;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.chartView.YAxisValueFormatter;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.markerInfoWindow.GoogleMapInfoWindow;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.SearchResultModel;
import com.grandtour.ev.evgrandtour.ui.settings.SettingsDialogView;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.chip.Chip;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentView extends BaseFragment<MapsFragmentContract.Presenter>
        implements MapsFragmentContract.View, OnMapReadyCallback, ClusterManager.OnClusterClickListener<MapCheckpoint>,
        CompoundButton.OnCheckedChangeListener, ClusterManager.OnClusterItemClickListener<MapCheckpoint>, GoogleMap.OnInfoWindowCloseListener {

    @NonNull
    public static final String TAG = MapsFragmentView.class.getSimpleName();

    @NonNull
    private static final String ACTION_ROUTE_BROADCAST = "RouteResultsBroadcast";
    @NonNull
    private static final String ACTION_LOCATION_BROADCAST = "LocationResultsBroadcast";
    public static final int ZOOM_LEVEL = 13;

    @NonNull
    private final MapsViewModel mapsViewModel = new MapsViewModel();

    @NonNull
    private final RouteRequestsBroadcastReceiver routeDirectionsBroadcastReceiver = new RouteRequestsBroadcastReceiver(getPresenter());
    @NonNull
    private final LocationUpdatesBroadcastReceiver locationUpdatesBroadcastReceiver = new LocationUpdatesBroadcastReceiver(getPresenter());
    @NonNull
    private final List<MapCheckpoint> filterSelection = new ArrayList<>();
    @Nullable
    private Marker userLocationMarker;
    @Nullable
    private Circle userLocationCircle;
    @NonNull
    public final ArrayList<Polyline> entireTripPolylineList = new ArrayList<>();

    @NonNull
    private FragmentMainMapViewBinding viewBinding;
    @Nullable
    private ClusterManager<MapCheckpoint> clusterManager;
    @NonNull
    private GoogleMap googleMap;

    @NonNull
    public static MapsFragmentView createInstance() {
        return new MapsFragmentView();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_map_view, container, false);
        viewBinding.setViewModel(mapsViewModel);
        viewBinding.setPresenter(getPresenter());

        viewBinding.mapView.onCreate(savedInstanceState);
        viewBinding.mapView.getMapAsync(this);
        setupFloatingActionButtonRevealHideAnimation();
        return viewBinding.getRoot();
    }

    private void setupFloatingActionButtonRevealHideAnimation() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.getRoot()
                .findViewById(R.id.bottomSheetRouteInfo));
        bottomSheetBehavior.setBottomSheetCallback(new RouteInfoBottomSheetCallback());
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (activity != null) {
            getPresenter().onUnBindDirectionsRequestService();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewBinding.mapView.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity)
                    .registerReceiver(routeDirectionsBroadcastReceiver, new IntentFilter(MapsFragmentView.ACTION_ROUTE_BROADCAST));
            LocalBroadcastManager.getInstance(activity)
                    .registerReceiver(locationUpdatesBroadcastReceiver, new IntentFilter(MapsFragmentView.ACTION_LOCATION_BROADCAST));
        }
    }

    @Override
    public void onPause() {
        viewBinding.mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewBinding.mapView.onDestroy();
        entireTripPolylineList.clear();
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity)
                    .unregisterReceiver(routeDirectionsBroadcastReceiver);
            LocalBroadcastManager.getInstance(activity)
                    .unregisterReceiver(locationUpdatesBroadcastReceiver);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        viewBinding.mapView.onLowMemory();
    }

    @Override
    public void showLoadingView(boolean isLoading) {
        if (isLoading) {
            mapsViewModel.isLoadingInProgress.set(true);
        } else {
            mapsViewModel.isLoadingInProgress.set(false);
        }
    }

    @Nullable
    @Override
    protected MapsFragmentPresenter createPresenter() {
        return new MapsFragmentPresenter(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Activity activity = getActivity();
        if (activity != null) {
            if (PermissionUtils.checkPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                setupGoogleMapsStyling();
                setupClusterManager(activity);
                setupGoogleMapCallbacks();
                getPresenter().onMapReady();
            } else {
                PermissionUtils.requestPermissionsInFragment(this, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private void setupGoogleMapsStyling() {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings()
                    .setMyLocationButtonEnabled(false);
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(Injection.provideGlobalContext(), R.raw.google_maps_dark_mode);
            googleMap.setMapStyle(style);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void setupClusterManager(@NonNull Activity activity) {
        clusterManager = new ClusterManager<>(activity, this.googleMap);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setRenderer(new MapsClusterRenderer(activity, googleMap, clusterManager));
        clusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new GoogleMapInfoWindow(activity));
    }

    private void setupGoogleMapCallbacks() {
        if (clusterManager != null) {
            googleMap.setOnCameraChangeListener(clusterManager);
            googleMap.setOnMarkerClickListener(clusterManager);
            googleMap.setOnInfoWindowClickListener(clusterManager);
            googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
            googleMap.setOnInfoWindowCloseListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.LOCATION_REQUEST_PERMISSION_ID && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewBinding.mapView.getMapAsync(this);
        }
    }

    @Override
    public void updateCurrentUserLocation(@NonNull LatLng latLng) {
        if (userLocationMarker != null && userLocationCircle != null) {
            userLocationMarker.setPosition(latLng);
            userLocationCircle.setCenter(latLng);
        } else {
            MarkerOptions userLocationMarkerOptions = MapUtils.getCurrentUserLocationMarker(latLng);
            CircleOptions userLocationCircleOptions = MapUtils.getCurrentUserLocationCircle(latLng);
            this.userLocationMarker = googleMap.addMarker(userLocationMarkerOptions);
            this.userLocationCircle = googleMap.addCircle(userLocationCircleOptions);
            AnimationManager.getInstance()
                    .startUserLocationAnimation(this.userLocationCircle);
        }
    }

    @Override
    public void loadCheckpointsOnMapView(@NonNull List<MapCheckpoint> checkpoints) {
        Activity activity = getActivity();
        if (clusterManager != null && activity != null) {
            clusterManager.clearItems();
            for (MapCheckpoint mapCheckpoint : checkpoints) {
                clusterManager.addItem(mapCheckpoint);
            }
        }
    }

    @Override
    public void centerMapToCurrentSelectedRoute(@NonNull List<MapCheckpoint> routeCheckpoints) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < routeCheckpoints.size(); i++) {
            LatLng position = routeCheckpoints.get(i)
                    .getPosition();
            builder.include(new LatLng(position.latitude, position.longitude));
        }
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
    }

    @Override
    public void clearMapCheckpoints() {
        if (clusterManager != null) {
            clusterManager.clearItems();
            clusterManager.cluster();
        }
    }

    @Override
    public void clearMapRoutes() {
        for (Polyline route : entireTripPolylineList) {
            route.remove();
        }
        entireTripPolylineList.clear();
    }

    @Override
    public void drawRouteStepLineOnMap(@NonNull PolylineOptions routePolyOptions, int routeStepId) {
        Activity activity = getActivity();
        if (activity != null) {
            Polyline routePolyline = googleMap.addPolyline(routePolyOptions);
            routePolyline.setTag(routeStepId);
            routePolyline.setClickable(true);
            entireTripPolylineList.add(routePolyline);
        }
    }

    @Override
    public void showTotalRouteInformation(@NonNull String routeDrivingDistance, @NonNull String routeDrivingDuration, @NonNull String routeTitle) {
        Context context = getContext();
        if (context != null) {
            mapsViewModel.routeDrivingDistance.set(routeDrivingDistance);
            mapsViewModel.routeDrivingDuration.set(routeDrivingDuration);
            mapsViewModel.routeTitle.set(routeTitle);
        }
    }

    @Override
    public void startGoogleMapsDirections(@NonNull String navigationUri) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUri));
        startActivity(mapIntent);
    }

    @Override
    public void showSettingsDialog() {
        SettingsDialogView dialog = new SettingsDialogView();
        showDialog(dialog, this, SettingsDialogView.TAG, 300);
    }

    @Override
    public void showTourPickerDialog() {
        ChooseTourDialogFragment dialog = ChooseTourDialogFragment.createInstance();
        showDialog(dialog, this, ChooseTourDialogFragment.TAG, 200);
    }

    @Override
    public void displaySearchResults(@NonNull List<SearchResultModel> checkpoints) {
        mapsViewModel.searchResultModels.update(checkpoints);
    }

    @Override
    public void clearSearchResults() {
        mapsViewModel.searchResultModels.update(new ArrayList<>());
    }

    @Override
    public void searchViewClosed() {
        mapsViewModel.isSearchViewOpen.set(false);
    }

    @Override
    public void searchViewOpen() {
        mapsViewModel.isSearchViewOpen.set(true);
    }

    @Override
    public void hideSoftKeyboard() {
        Activity activity = getActivity();
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0); // hide
            }
        }
    }

    @Override
    public void animateRouteSelectionButton() {
        mapsViewModel.isButtonBouncing.set(true);
    }

    @Override
    public void animateRouteInformationText() {
        mapsViewModel.isWarningState.set(true);
    }

    @Override
    public void showChartView(@NonNull LineData lineData, @NonNull Description description) {
        LineChart chartView = viewBinding.getRoot()
                .findViewById(R.id.routeElevationChart);
        chartView.setDescription(description);
        chartView.setData(lineData);
        chartView.getLegend()
                .setTextColor(Color.WHITE);
        chartView.invalidate();
        chartView.animateY(1000);
        setupChartViewAxisStyling(chartView);
    }

    @Override
    public void loadAvailableFilterPoints(@NonNull List<MapCheckpoint> availableFilterPoints) {
        Activity activity = getActivity();
        if (activity != null) {
            mapsViewModel.checkPointFilteringOptions.clear();
            // TODO QuickFix, need to implement proper clearing logic in ViewModel
            viewBinding.chipGroupFilteringOptions.removeAllViews();
            for (MapCheckpoint mapCheckpoint : availableFilterPoints) {
                String filterOptionTitle = getString(R.string.format_filter_option, mapCheckpoint.getOrderInRouteId(), mapCheckpoint.getMapCheckpointTitle());
                Chip filterChip = new Chip(activity);
                filterChip.setTag(mapCheckpoint);
                filterChip.setCheckable(true);
                filterChip.setText(filterOptionTitle);
                filterChip.setOnCheckedChangeListener(this);
                mapsViewModel.checkPointFilteringOptions.add(filterChip);
            }
        }
    }

    private void setupChartViewAxisStyling(@NonNull LineChart chartView) {
        XAxis xAxis = chartView.getXAxis();
        YAxis yAxisLeft = chartView.getAxisLeft();
        YAxis yAxisRight = chartView.getAxisRight();

        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setValueFormatter(new YAxisValueFormatter());
        yAxisRight.setEnabled(false);

        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new XAxisValueFormatter());
    }

    @Override
    public void showFilteringOptionsView() {
        boolean areFilteringOptionsVisible = mapsViewModel.isFilteringLayoutVisible.get();
        if (areFilteringOptionsVisible) {
            mapsViewModel.isFilteringLayoutVisible.set(false);
            getPresenter().onClearFilteredRouteClicked();
        } else {
            mapsViewModel.isFilteringLayoutVisible.set(true);
        }
    }

    @Override
    public void clearFilteringChipsSelectionStatus() {
        // TODO Use Databinding here in the ViewModel
        for (Chip filterChipOption : mapsViewModel.checkPointFilteringOptions) {
            filterChipOption.setChecked(false);
        }
        filterSelection.clear();
    }

    @Override
    public void moveCameraToCurrentLocation(@NonNull LatLng location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, MapsFragmentView.ZOOM_LEVEL));
    }

    @Override
    public void showSelectTripButton(boolean shouldDisplaySelectTripButton) {
        mapsViewModel.isSelectTourButtonDisplayed.set(shouldDisplaySelectTripButton);
    }

    @Override
    public void showNavigationButton(boolean shouldDisplayNavigationLayout) {
        mapsViewModel.areNavigationButtonsEnabled.set(shouldDisplayNavigationLayout);
    }

    @Override
    public void highLightNavigationPath(List<Integer> routeLegsIdsToHighLight) {
        for (Integer routeLegId : routeLegsIdsToHighLight) {
            for (Polyline polyline : entireTripPolylineList) {
                Integer routeIdInPolyline = (Integer) polyline.getTag();
                if (routeIdInPolyline != null) {
                    if (routeIdInPolyline.equals(routeLegId)) {
                        polyline.setColor(Injection.provideResources()
                                .getColor(R.color.colorBlue));
                    }
                }
            }
        }
    }

    @Override
    public void clearAllHighlightedPaths() {
        for (Polyline polyline : entireTripPolylineList) {
            polyline.setColor(Injection.provideResources()
                    .getColor(R.color.colorAccent));
        }
    }

    @Override
    public void OnLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled) {
        getPresenter().onLocationTrackingSettingsUpdate(isLocationTrackingEnabled);
    }

    @Override
    public void OnSelectedTour(@NonNull String tourId, List<TourDataResponse> tourDataResponses) {
        getPresenter().onTourSelected(tourId, tourDataResponses);
    }

    @Override
    public boolean onClusterClick(Cluster<MapCheckpoint> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        return true;
    }

    // TODO Implement this in a callback via a DataBinding adapter
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView instanceof Chip) {
            MapCheckpoint selectedFilterCheckpoint = (MapCheckpoint) buttonView.getTag();
            if (selectedFilterCheckpoint != null) {
                if (isChecked) {
                    if (filterSelection.size() < 2) {
                        filterSelection.add(selectedFilterCheckpoint);
                        if (filterSelection.size() == 2) {
                            getPresenter().onSelectedCheckpointRouteFilters(filterSelection);
                        }
                    } else {
                        buttonView.setChecked(false);
                    }
                }
                if (!isChecked) {
                    if (filterSelection.size() == 2) {
                        getPresenter().onFilterChipSelectionRemoved();
                    }
                    filterSelection.remove(selectedFilterCheckpoint);
                }
            }
        }
    }

    @Override
    public boolean onClusterItemClick(MapCheckpoint mapCheckpoint) {
        if (mapCheckpoint != null) {
            getPresenter().onMarkerClicked(mapCheckpoint.getMapCheckpointId());
        }
        return false;
    }

    /**
     * Be aware, marker object is always null here due to the fact that we are redirecting the info window callbacks via the cluster manager in MapUtils,
     * that means the googleMap callback does not return any marker object upon info window closed callback
     */
    @Override
    public void onInfoWindowClose(Marker marker) {
        getPresenter().onMarkerInfoWindowClosed();
    }

    private class RouteInfoBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View view, int newState) {
            // this part hides the button immediately and waits bottom sheet
            if (BottomSheetBehavior.STATE_DRAGGING == newState) {
                mapsViewModel.isSelectTourButtonDisplayed.set(false);
            } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                mapsViewModel.isSelectTourButtonDisplayed.set(true);
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {
        }
    }
}

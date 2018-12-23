package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.databinding.FragmentMainMapViewBinding;
import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.base.BaseFragment;
import com.grandtour.ev.evgrandtour.ui.chooseTour.ChooseTourDialogFragment;
import com.grandtour.ev.evgrandtour.ui.distancePicker.DistancePickerDialogFragment;
import com.grandtour.ev.evgrandtour.ui.elevationView.ElevationChartFragment;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.broadcastReceivers.LocationUpdatesBroadcastReceiver;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.broadcastReceivers.RouteRequestsBroadcastReceiver;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.markerInfoWindow.GoogleMapInfoWindow;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.CurrentUserLocation;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.search.SearchResultViewModel;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.search.SearchResultsListViewModel;
import com.grandtour.ev.evgrandtour.ui.settings.SettingsDialogView;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
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
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentView extends BaseFragment
        implements MapsFragmentContract.View, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolylineClickListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener, ClusterManager.OnClusterItemInfoWindowClickListener<MapCheckpoint>,
        ClusterManager.OnClusterClickListener<MapCheckpoint> {

    @NonNull
    private static final String ACTION_ROUTE_BROADCAST = "RouteResultsBroadcast";
    @NonNull
    private static final String ACTION_LOCATION_BROADCAST = "LocationResultsBroadcast";

    public static final int ZOOM_LEVEL = 13;
    @NonNull
    public static final String TAG = MapsFragmentView.class.getSimpleName();
    @NonNull
    private final MapsViewModel mapsViewModel = new MapsViewModel();
    @NonNull
    private final SearchResultsListViewModel searchResultViewModel = new SearchResultsListViewModel();

    @NonNull
    private final MapsFragmentPresenter presenter = new MapsFragmentPresenter(this);
    @NonNull
    private final RouteRequestsBroadcastReceiver routeDirectionsBroadcastReceiver = new RouteRequestsBroadcastReceiver(presenter);
    @NonNull
    private final LocationUpdatesBroadcastReceiver locationUpdatesBroadcastReceiver = new LocationUpdatesBroadcastReceiver(presenter);
    @NonNull
    private GoogleMap googleMap;
    @NonNull
    private FragmentMainMapViewBinding viewBinding;

    @Nullable
    private ClusterManager<MapCheckpoint> clusterManager;

    @NonNull
    public static MapsFragmentView createInstance() {
        return new MapsFragmentView();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_map_view, container, false);
        viewBinding.setViewModel(mapsViewModel);
        viewBinding.setSearchViewModel(searchResultViewModel);
        viewBinding.setPresenter(presenter);

        viewBinding.searchViewCheckpoints.setOnQueryTextListener(this);
        viewBinding.searchViewCheckpoints.setOnSearchClickListener(this);
        viewBinding.searchViewCheckpoints.setOnCloseListener(this);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter.onAttach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (activity != null) {
            presenter.onUnBindDirectionsRequestService();
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
        mapsViewModel.routeCheckpoints.clear();
        mapsViewModel.routePolyLine.clear();
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity)
                    .unregisterReceiver(routeDirectionsBroadcastReceiver);
            LocalBroadcastManager.getInstance(activity)
                    .unregisterReceiver(locationUpdatesBroadcastReceiver);
        }
        presenter.onDestroy();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        presenter.onAttach();
        Activity activity = getActivity();
        if (activity != null) {
            if (PermissionUtils.checkPermissions(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                this.googleMap.setMyLocationEnabled(true);
                setupClusterManager(activity);
                setupGoogleMapCallbacks();
                presenter.onMapReady();
            } else {
                PermissionUtils.requestPermissionsInFragment(this, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void setupClusterManager(@NonNull Activity activity) {
        clusterManager = new ClusterManager<>(activity, this.googleMap);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
        clusterManager.setOnClusterClickListener(this);
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
        }
        googleMap.setOnPolylineClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.LOCATION_REQUEST_PERMISSION_ID && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewBinding.mapView.getMapAsync(this);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mapsViewModel.currentSelectedMarker.set(marker);
        return false;
    }

    @Override
    public void updateCurrentUserLocation(@NonNull LatLng latLng) {
        CurrentUserLocation currentCurrentUserLocation = mapsViewModel.currentUserLocation.get();
        if (currentCurrentUserLocation != null) {
            currentCurrentUserLocation.getCurrentLocationMarker()
                    .setPosition(latLng);
            currentCurrentUserLocation.getCurrentLocationCircle()
                    .setCenter(latLng);
        } else {
            MarkerOptions markerOptions = MapUtils.getCurrentUserLocationMarker(latLng);
            CircleOptions circleOptions = MapUtils.getCurrentUserLocationCircle(latLng);

            Marker currentUserMarker = googleMap.addMarker(markerOptions);
            Circle currentUserCircle = googleMap.addCircle(circleOptions);

            mapsViewModel.currentUserLocation.set(new CurrentUserLocation(currentUserMarker, currentUserCircle));
            AnimationManager.getInstance()
                    .startUserLocationAnimation(currentUserCircle);
        }
    }

    @Override
    public void loadCheckpoints(@NonNull List<MapCheckpoint> checkpoints) {
        Activity activity = getActivity();
        if (clusterManager != null && activity != null) {
            clusterManager.clearItems();
            mapsViewModel.routeCheckpoints.clear();
            for (MapCheckpoint mapCheckpoint : checkpoints) {
                clusterManager.addItem(mapCheckpoint);
                mapsViewModel.routeCheckpoints.add(mapCheckpoint);
            }
        }
    }

    @Override
    public void centerMapToCurrentSelectedRoute() {
        List<MapCheckpoint> routeCheckpoints = mapsViewModel.routeCheckpoints;
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
    public void moveToMarker(@NonNull Integer markerCheckpointId) {
        for (MapCheckpoint checkpoint : mapsViewModel.routeCheckpoints) {
            Integer checkpointId = checkpoint.getMapCheckpointId();
            if (checkpointId.equals(markerCheckpointId)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(checkpoint.getPosition(), MapsFragmentView.ZOOM_LEVEL));
            }
        }
    }

    @Override
    public void clearMapCheckpoints() {
        if (clusterManager != null) {
            clusterManager.clearItems();
            mapsViewModel.routeCheckpoints.clear();
            clusterManager.cluster();
        }
    }

    @Override
    public void clearMapRoutes() {
        for (Polyline route : mapsViewModel.routePolyLine) {
            route.remove();
        }
        mapsViewModel.routePolyLine.clear();
    }

    @Override
    public void drawRouteStepLineOnMap(@NonNull PolylineOptions routePolyOptions, int routeStepId) {
        Activity activity = getActivity();
        if (activity != null) {
            Polyline routePolyline = googleMap.addPolyline(routePolyOptions);
            routePolyline.setTag(routeStepId);
            routePolyline.setClickable(true);
            mapsViewModel.routePolyLine.add(routePolyline);
        }
    }

    @Override
    public void showTotalRouteInformation(@NonNull String routeTitle, @NonNull String routeInfo) {
        Context context = getContext();
        if (context != null) {
            mapsViewModel.routeInformation.set(routeInfo);
            mapsViewModel.routeTitle.set(routeTitle);
        }
    }

    @Override
    public void startGoogleMapsDirections(@NonNull String navigationUri) {
         Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUri));
         startActivity(mapIntent);
    }

    @Override
    public void showCalculateDistanceDialog(@NonNull List<Checkpoint> checkpoints) {
        DistancePickerDialogFragment dialog = new DistancePickerDialogFragment();
        dialog.setTotalCheckpoints(checkpoints);
        showDialog(dialog, this, DistancePickerDialogFragment.TAG, 400);
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
    public void displaySearchResults(@NonNull List<SearchResultViewModel> checkpoints) {
        searchResultViewModel.parameters.update(checkpoints);
    }

    @Override
    public void clearSearchResults() {
        searchResultViewModel.parameters.update(new ArrayList<>());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        if (searchQuery.equals("")) {
            presenter.onSearchQueryCleared();
        } else {
            presenter.onNewSearchQuery(searchQuery);
        }
        return false;
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
    public void showElevationChartForRouteLegDialog(@NonNull Integer routeLegId) {
        ElevationChartFragment elevationInfoFragment = ElevationChartFragment.newChartForRouteLegInstance(routeLegId);
        showDialog(elevationInfoFragment, this, ElevationChartFragment.TAG, 500);
    }

    @Override
    public void showEntireRouteElevationChartDialog() {
        ElevationChartFragment elevationInfoFragment = ElevationChartFragment.newChartForEntireRouteInstance();
        showDialog(elevationInfoFragment, this, ElevationChartFragment.TAG, 500);
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
    }

    public void onCalculateDistanceBetweenCheckpoints() {
        presenter.onCalculateDistanceBetweenTwoCheckpointsClicked();
    }

    public void openSettingsDialog() {
        presenter.onSettingsClicked();
    }

    public void openEntireTourElevationChart() {
        presenter.onRouteElevationChartClicked();
    }

    @Override
    public void OnLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled) {
        presenter.onLocationTrackingSettingsUpdate(isLocationTrackingEnabled);
    }

    @Override
    public void OnSelectedTour(@NonNull String tourId, List<TourDataResponse> tourDataResponses) {
        presenter.onTourSelected(tourId, tourDataResponses);
    }

    @Override
    public boolean onClose() {
        mapsViewModel.isSearchViewOpen.set(false);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchViewCheckpoints:
                mapsViewModel.isSearchViewOpen.set(true);
                break;
        }
    }

    @Override
    public void onClusterItemInfoWindowClick(MapCheckpoint mapCheckpoint) {
        presenter.onNavigationClicked(mapCheckpoint);
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

    @Override
    public void onPolylineClick(Polyline polyline) {
        Integer routeLegId = (Integer) polyline.getTag();
        presenter.onPolylineClicked(routeLegId);
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

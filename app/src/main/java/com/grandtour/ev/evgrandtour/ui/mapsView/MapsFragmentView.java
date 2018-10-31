package com.grandtour.ev.evgrandtour.ui.mapsView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;
import com.grandtour.ev.evgrandtour.databinding.FragmentMainMapViewBinding;
import com.grandtour.ev.evgrandtour.services.LocationsUpdatesService;
import com.grandtour.ev.evgrandtour.services.RouteDirectionsRequestsService;
import com.grandtour.ev.evgrandtour.ui.mapsView.broadcastReceivers.LocationUpdatesBroadcastReceiver;
import com.grandtour.ev.evgrandtour.ui.mapsView.broadcastReceivers.RouteRequestsBroadcastReceiver;
import com.grandtour.ev.evgrandtour.ui.mapsView.distancePickerDialog.DistancePickerDialogFragment;
import com.grandtour.ev.evgrandtour.ui.mapsView.markerInfo.GoogleMapInfoWindow;
import com.grandtour.ev.evgrandtour.ui.mapsView.models.UserLocation;
import com.grandtour.ev.evgrandtour.ui.mapsView.search.SearchResultViewModel;
import com.grandtour.ev.evgrandtour.ui.mapsView.search.SearchResultsListViewModel;
import com.grandtour.ev.evgrandtour.ui.mapsView.settingsDialog.SettingsDialogView;
import com.grandtour.ev.evgrandtour.ui.utils.AnimationUtils;
import com.grandtour.ev.evgrandtour.ui.utils.DialogUtils;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentView extends Fragment
        implements MapsFragmentContract.View, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, SearchView.OnQueryTextListener {

    public static final int ZOOM_LEVEL = 13;
    @NonNull
    public static final String TAG = MapsFragmentView.class.getSimpleName();
    @NonNull
    private GoogleMap googleMap;
    @NonNull
    private MapView mapView;
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
    public static MapsFragmentView createInstance() {
        return new MapsFragmentView();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainMapViewBinding mapFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_map_view, container, false);
        mapFragmentBinding.setViewModel(mapsViewModel);
        mapFragmentBinding.setSearchViewModel(searchResultViewModel);
        mapFragmentBinding.setPresenter(presenter);
        mapFragmentBinding.searchViewCheckpoints.setOnQueryTextListener(this);
        mapView = mapFragmentBinding.getRoot()
                .findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return mapFragmentBinding.getRoot();
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
        mapView.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity)
                    .registerReceiver(routeDirectionsBroadcastReceiver, new IntentFilter(RouteDirectionsRequestsService.ACTION_ROUTE_BROADCAST));
            LocalBroadcastManager.getInstance(activity)
                    .registerReceiver(locationUpdatesBroadcastReceiver, new IntentFilter(LocationsUpdatesService.ACTION_LOCATION_BROADCAST));
        }
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mapsViewModel.checkpoints.clear();
        mapsViewModel.routes.clear();
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
        mapView.onLowMemory();
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
    public void showMessage(@NonNull String msg) {
        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
        presenter.onAttach();
        Activity activity = getActivity();
        if (activity != null) {
            if (PermissionUtils.checkPermissions(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                this.googleMap.setMyLocationEnabled(true);
                this.googleMap.setInfoWindowAdapter(new GoogleMapInfoWindow(getActivity()));
                this.googleMap.setOnInfoWindowLongClickListener(this);
                presenter.onMapReady();
            } else {
                PermissionUtils.requestPermissionsInFragment(this, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.LOCATION_REQUEST_PERMISSION_ID && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(this);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mapsViewModel.currentSelectedMarker.set(marker);
        return false;
    }

    @Override
    public void updateCurrentUserLocation(@NonNull LatLng latLng) {
        UserLocation currentUserLocation = mapsViewModel.currentUserLocation.get();
        if (currentUserLocation != null) {
            currentUserLocation.getCurrentLocationMarker().setPosition(latLng);
            currentUserLocation.getCurrentLocationCircle().setCenter(latLng);
        } else {
            MarkerOptions markerOptions = MapUtils.getCurrentUserLocationMarker(latLng);
            CircleOptions circleOptions = MapUtils.getCurrentUserLocationCircle(latLng);

            Marker currentUserMarker = googleMap.addMarker(markerOptions);
            Circle currentUserCircle = googleMap.addCircle(circleOptions);

            mapsViewModel.currentUserLocation.set(new UserLocation(currentUserMarker, currentUserCircle));
            AnimationUtils.addAnimationToCircle(currentUserCircle);
        }
    }

    @Override
    public void loadCheckpoints(@NonNull List<Pair<Integer, MarkerOptions>> checkpoints) {
        for (Pair<Integer, MarkerOptions> checkpoint : checkpoints) {
            Marker checkpointMarker = googleMap.addMarker(checkpoint.second);
            checkpointMarker.setTag(checkpoint.first);
            mapsViewModel.checkpoints.add(checkpointMarker);
        }
    }

    @Override
    public void centerMapToCurrentSelectedRoute() {
        List<Marker> routeCheckpoints = mapsViewModel.checkpoints;
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
        for (Marker checkpoint : mapsViewModel.checkpoints) {
            Integer checkpointId = (Integer) checkpoint.getTag();
            if (checkpointId != null) {
                if (checkpointId.equals(markerCheckpointId)) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(checkpoint.getPosition(), MapsFragmentView.ZOOM_LEVEL));
                }
            }
        }
    }

    @Override
    public void clearMapCheckpoints() {
        for (Marker checkpoint : mapsViewModel.checkpoints) {
            checkpoint.remove();
        }
        mapsViewModel.checkpoints.clear();
    }

    @Override
    public void clearMapRoutes() {
        for (Polyline route : mapsViewModel.routes) {
            route.remove();
        }
        mapsViewModel.routes.clear();
    }

    @Override
    public void drawCheckpointsRoute(@NonNull PolylineOptions routePolyOptions) {
        routePolyOptions.color(getContext().getResources()
                .getColor(R.color.colorSecondaryGreen));

        Polyline route = googleMap.addPolyline(routePolyOptions);
        mapsViewModel.routes.add(route);
    }

    @Override
    public void showTotalRouteInformation(@NonNull String infoMessage, boolean shouldShowInfoCard) {
        Context context = getContext();
        if (context != null) {
            if (shouldShowInfoCard) {
                mapsViewModel.isRouteLengthAvailable.set(true);
                mapsViewModel.totalRouteInformation.set(infoMessage);
            } else {
                mapsViewModel.totalRouteInformation.set("");
                mapsViewModel.isRouteLengthAvailable.set(false);
            }
        }
    }

    @Override
    public void startGoogleMapsDirections(@NonNull String navigationUri) {
         Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUri));
         startActivity(mapIntent);
    }

    @Override
    public void showCalculateDistanceDialog(@NonNull List<Checkpoint> checkpoints) {
        FragmentManager fManager = getFragmentManager();
        if (fManager != null) {
            FragmentTransaction fragmentTransaction = fManager.beginTransaction();
            Fragment previousDialog = getFragmentManager().findFragmentByTag(DistancePickerDialogFragment.TAG);
            if (previousDialog != null) {
                fragmentTransaction.remove(previousDialog);
            }
            fragmentTransaction.addToBackStack(null);
            DistancePickerDialogFragment dialogFragment = new DistancePickerDialogFragment();
            dialogFragment.setTotalCheckpoints(checkpoints);
            dialogFragment.show(fragmentTransaction, DistancePickerDialogFragment.TAG);
        }
    }

    @Override
    public void showSettingsDialog() {
        FragmentManager fManager = getFragmentManager();
        if (fManager != null) {
            FragmentTransaction fragmentTransaction = fManager.beginTransaction();
            Fragment previousDialog = getFragmentManager().findFragmentByTag(SettingsDialogView.TAG);
            if (previousDialog != null) {
                fragmentTransaction.remove(previousDialog);
            }
            fragmentTransaction.addToBackStack(null);
            SettingsDialogView dialogFragment = new SettingsDialogView();
            dialogFragment.setTargetFragment(MapsFragmentView.this, 300);
            dialogFragment.show(fragmentTransaction, SettingsDialogView.TAG);
        }
    }

    @Override
    public void showTourPickerDialog(@NonNull List<TourDataResponse> tours) {
        Activity activity = getActivity();
        if (activity != null) {
            final String[] selectedTourId = new String[1];
            DialogUtils.getMultipleChoicesAlertDialogBuilder(getActivity(), tours, getString(R.string.title_select_tour), tourId -> selectedTourId[0] = tourId)
                    .setPositiveButton("OK", (dialog, which) -> {
                        presenter.onTourSelected(selectedTourId[0]);
                    })
                    .show();
        }
    }

    @Override
    public void displaySearchResults(@NonNull List<SearchResultViewModel> checkpoints) {
        searchResultViewModel.parameters.clear();
        searchResultViewModel.parameters.addAll(checkpoints);
    }

    @Override
    public void clearSearchResults() {
        searchResultViewModel.parameters.clear();
        searchResultViewModel.parameters.addAll(new ArrayList<>());
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
    public void onInfoWindowLongClick(Marker navigateToMarker) {
        presenter.onNavigationClicked(navigateToMarker);
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

    /** Delegated methods from the main activity
     */
    public void clearMapDataClicked() {
        Context context = getContext();
        if (context != null) {
            DialogUtils.getAlertDialogBuilder(context, getString(R.string.message_are_you_sure_you_want_to_delete_all_data),
                    getString(R.string.title_are_you_sure))
                    .setPositiveButton(getString(R.string.btn_ok), (dialog, which) -> presenter.onClearCheckpointsAndRoutesClicked())
                    .setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        }
    }

    public void onChooseTourClicked() {
        presenter.onChooseTourClicked();
    }

    public void onCalculateDistanceBetweenCheckpoints() {
        presenter.onCalculateDistanceBetweenTwoCheckpointsClicked();
    }

    public void openSettingsDialog() {
        presenter.onSettingsClicked();
    }

    @Override
    public void OnLocationTrackingSettingsUpdate(boolean isLocationTrackingEnabled) {
        presenter.onLocationTrackingSettingsUpdate(isLocationTrackingEnabled);
    }

}

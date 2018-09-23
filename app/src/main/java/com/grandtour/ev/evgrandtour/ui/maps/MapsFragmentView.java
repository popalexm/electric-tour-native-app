package com.grandtour.ev.evgrandtour.ui.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.MapFragmentBinding;
import com.grandtour.ev.evgrandtour.services.LocationUpdatesService;
import com.grandtour.ev.evgrandtour.ui.maps.models.UserLocation;
import com.grandtour.ev.evgrandtour.ui.utils.AnimationUtils;
import com.grandtour.ev.evgrandtour.ui.utils.DialogUtils;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapsFragmentView extends Fragment implements MapsFragmentContract.View, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    @NonNull
    public static final String TAG = MapsFragmentView.class.getSimpleName();
    private static final int OPEN_LOCAL_STORAGE_CODE = 100;
    @NonNull
    private static final String OPEN_NAVIGATION_URI = "google.navigation:q=";
    @NonNull
    private static final String MAPS_PACKECGE_NAME = "com.google.android.apps.maps";

    @NonNull
    private final MapsViewModel mapsViewModel = new MapsViewModel();
    @NonNull
    private GoogleMap googleMap;
    @NonNull
    private MapView mapView;
    @NonNull
    private final MapsFragmentPresenter presenter = new MapsFragmentPresenter(this);
    ;


    @NonNull
    private final LocationUpdatesReceiver locationUpdatesReceiver = new LocationUpdatesReceiver();
    @Nullable
    private UserLocation currentUserLocation;
    @Nullable
    private LatLng currentSelectedCheckpoint;

    @NonNull
    private final List<Marker> checkpoints = new ArrayList<>();
    @NonNull
    private final List<Polyline> routes = new ArrayList<>();

    @NonNull
    public static MapsFragmentView createInstance() {
        return new MapsFragmentView();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MapFragmentBinding mapFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.map_fragment, container, false);
        mapFragmentBinding.setViewModel(mapsViewModel);
        mapFragmentBinding.setPresenter(presenter);
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
        Activity activity = getActivity();
        if (activity != null) {
            presenter.onStartLocationService(activity);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Activity activity = getActivity();
        if (activity != null) {
            presenter.onStopLocationService(activity);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity).registerReceiver(locationUpdatesReceiver,
                    new IntentFilter(LocationUpdatesService.ACTION_LOCATION_INFO_BROADCAST));
        }
    }

    @Override
    public void onPause() {
        Activity activity = getActivity();
        if (activity != null) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(locationUpdatesReceiver);
        }
        mapView.onPause();
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        checkpoints.clear();
        routes.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MapsFragmentView.OPEN_LOCAL_STORAGE_CODE && resultCode == RESULT_OK) {
            if (data!= null) {
                Uri uri = data.getData();
                if (uri != null) {
                    presenter.onLocalFileOpened(uri);
                }
            }
        }
    }

    @Override
    public void showLoadingView(boolean isLoading, boolean isCancelable, @NonNull String msg) {
        if (isLoading) {
            mapsViewModel.isLoadingInProgress.set(true);
            mapsViewModel.progressMessage.set(msg);
            if (isCancelable) {
                mapsViewModel.isCancelEnabled.set(true);
            } else {
                mapsViewModel.isCancelEnabled.set(false);
            }
        } else {
            mapsViewModel.isLoadingInProgress.set(false);
        }
    }

    @Override
    public void showMessage(@NonNull String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                presenter.onMapReady();
            } else {
                PermissionUtils.requestPermissions(activity, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
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
        currentSelectedCheckpoint = marker.getPosition();
        return false;
    }

    @Override
    public void updateCurrentUserLocation(@NonNull LatLng latLng) {
        if (currentUserLocation != null) {
            currentUserLocation.getCurrentLocationMarker().setPosition(latLng);
            currentUserLocation.getCurrentLocationCircle().setCenter(latLng);
        } else {
            MarkerOptions markerOptions = MapUtils.getCurrentUserLocationMarker(latLng);
            CircleOptions circleOptions = MapUtils.getCurrentUserLocationCircle(latLng);

            Marker currentUserMarker = googleMap.addMarker(markerOptions);
            Circle currentUserCircle = googleMap.addCircle(circleOptions);

            currentUserLocation = new UserLocation(currentUserMarker , currentUserCircle);
            AnimationUtils.addAnimationToCircle(currentUserCircle);
        }
    }

    @Override
    public void loadCheckpoints(@NonNull List<Pair<Integer, MarkerOptions>> checkpoints) {
        for (Pair<Integer, MarkerOptions> checkpoint : checkpoints) {
            Marker checkpointMarker = googleMap.addMarker(checkpoint.second);
            checkpointMarker.setTag(checkpoint.first);
            this.checkpoints.add(checkpointMarker);
        }
    }

    @Override
    public void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        try {
            startActivityForResult(intent, MapsFragmentView.OPEN_LOCAL_STORAGE_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearMapCheckpoints() {
        for (Marker waypoint : checkpoints) {
            waypoint.remove();
        }
        checkpoints.clear();
    }

    @Override
    public void clearMapRoutes() {
        for (Polyline route : routes) {
            route.remove();
        }
        routes.clear();
    }

    @Override
    public void drawCheckpointsRoute(@NonNull PolylineOptions routePolyOptions) {
        Polyline route = googleMap.addPolyline(routePolyOptions);
        routes.add(route);
    }

    @Override
    public void showTotalRouteLength(int length) {
        Context context = getContext();
        if (context != null) {
            String msg = getString(R.string.format_start_number_end_message, getString(R.string.message_total_route_lenght_is_estimated_at), length,
                    getString(R.string.suffix_kilometers));
            DialogUtils.getAlertDialogBuilder(context, msg, getString(R.string.title_route_length))
                    .setPositiveButton(getString(R.string.btn_dimiss), (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    public void openNavigationForSelectedMarker() {
        if (currentSelectedCheckpoint != null) {
            startNavigation(currentSelectedCheckpoint);
        } else {
            showMessage(getString(R.string.message_no_checkpoint_selected));
        }
    }

    /**
     * Starts the google maps Navigation Mode for the selected marker
     */
    private void startNavigation(@NonNull LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        String navUri = getString(R.string.format_navigation_directions, MapsFragmentView.OPEN_NAVIGATION_URI, latitude, longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navUri));
        mapIntent.setPackage(MapsFragmentView.MAPS_PACKECGE_NAME);
        startActivity(mapIntent);
    }

    /** Delegated methods from the main activity
     */
    public void clearMapDataClicked() {
        Context context = getContext();
        if (context != null) {
            DialogUtils.getAlertDialogBuilder(context, getString(R.string.message_are_you_sure_you_want_to_delete_all_data),
                    getString(R.string.title_are_you_sure))
                    .setPositiveButton("Yes", (dialog, which) -> presenter.onClearCheckpointsAndRoutesClicked())
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        }
    }

    public void calculateRoutesClicked(){
        presenter.onCalculateRoutesClicked();
    }

    public void onTotalRouteLenghtClicked() {
        presenter.onTotalRouteInfoClicked();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_route_calculations:
                presenter.onStopCalculatingRoutesClicked();
                break;
        }
    }

    class LocationUpdatesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Location location = intent.getParcelableExtra(LocationUpdatesService.LOCATION_EXTRA_INFORMATION);
                if (location != null) {
                    presenter.onCurrentLocationChanged(location);
                }
            }
        }
    }
}

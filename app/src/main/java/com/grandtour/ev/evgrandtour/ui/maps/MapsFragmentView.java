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
import com.grandtour.ev.evgrandtour.services.LocationUpdatesService;
import com.grandtour.ev.evgrandtour.ui.maps.models.UserLocation;
import com.grandtour.ev.evgrandtour.ui.utils.AnimationUtils;
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
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapsFragmentView extends Fragment implements MapsFragmentContract.View, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    @NonNull
    public static final String TAG = MapsFragmentView.class.getSimpleName();
    private static final int OPEN_LOCAL_STORAGE_CODE = 100;
    private static final String OPEN_NAVIGATION_URI = "google.navigation:q=";

    @NonNull
    private GoogleMap googleMap;
    @NonNull
    private MapView mapView;
    @Nullable
    private LinearLayout loadingLayout;

    @NonNull
    private MapsFragmentPresenter presenter;
    @NonNull
    private final LocationUpdatesReceiver locationUpdatesReceiver = new LocationUpdatesReceiver();
    @Nullable
    private UserLocation currentUserLocation;
    @Nullable
    private LatLng currentSelectedCheckpoint;

    @NonNull
    private final List<Marker> waypoints = new ArrayList<>();
    @NonNull
    private final List<Polyline> routes = new ArrayList<>();

    @NonNull
    public static MapsFragmentView createInstance() {
        return new MapsFragmentView();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        loadingLayout = view.findViewById(R.id.info_layout);
        loadingLayout.setVisibility(View.GONE);
        Button cancelBtn = view.findViewById(R.id.btn_cancel_route_calculations);
        cancelBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter = new MapsFragmentPresenter(this);
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
        presenter.onDetach();
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
    public void showLoadingStatus(boolean isLoading, @NonNull String msg) {
        if (loadingLayout != null) {
            if (isLoading) {
                loadingLayout.setVisibility(View.VISIBLE);
                ((TextView) loadingLayout.getChildAt(1)).setText(msg);
            } else {
                loadingLayout.setVisibility(View.GONE);
            }
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
    public void loadDestinations(@NonNull List<MarkerOptions> destinations) {
        for (MarkerOptions markerOptions : destinations) {
            Marker waypointMarker = googleMap.addMarker(markerOptions);
            waypoints.add(waypointMarker);
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
    public void clearMapWaypoints() {
        for (Marker waypoint : waypoints) {
            waypoint.remove();
        }
    }

    @Override
    public void clearMapRoutes() {
        for (Polyline route : routes) {
            route.remove();
        }
    }

    @Override
    public void drawWaypointRoute(@NonNull PolylineOptions routePolyOptions) {
        Polyline route = googleMap.addPolyline(routePolyOptions);
        routes.add(route);
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
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    /** Delegated methods from the main activity
     */
    public void clearWaypointsClicked(){
        presenter.onClearWaypointsClicked();
    }

    public void calculateRoutesClicked(){
        presenter.onCalculateRoutesClicked(waypoints);
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

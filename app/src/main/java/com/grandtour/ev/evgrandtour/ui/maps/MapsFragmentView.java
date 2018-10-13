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
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.databinding.FragmentMainMapViewBinding;
import com.grandtour.ev.evgrandtour.services.RouteDirectionsRequestsService;
import com.grandtour.ev.evgrandtour.ui.maps.dialog.DistancePickerDialogFragment;
import com.grandtour.ev.evgrandtour.ui.maps.models.UserLocation;
import com.grandtour.ev.evgrandtour.ui.utils.AnimationUtils;
import com.grandtour.ev.evgrandtour.ui.utils.DialogUtils;
import com.grandtour.ev.evgrandtour.ui.utils.MapUtils;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MapsFragmentView extends Fragment implements MapsFragmentContract.View, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    @NonNull
    public static final String TAG = MapsFragmentView.class.getSimpleName();
    private static final int OPEN_LOCAL_STORAGE_CODE = 100;
    @NonNull
    private static final String OPEN_NAVIGATION_URI = "google.navigation:q=";
    @NonNull
    private static final String OPEN_NAVIGATION_MAPS_PACKAGE_NAME = "com.google.android.apps.maps";

    @NonNull
    private GoogleMap googleMap;
    @NonNull
    private MapView mapView;
    @NonNull
    private final MapsViewModel mapsViewModel = new MapsViewModel();
    @NonNull
    private final MapsFragmentPresenter presenter = new MapsFragmentPresenter(this);
    @NonNull
    private final DirectionsRequestsReceiver routeDirectionsRequestsService = new DirectionsRequestsReceiver(presenter);

    @NonNull
    public static MapsFragmentView createInstance() {
        return new MapsFragmentView();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMainMapViewBinding mapFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_map_view, container, false);
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
                    .registerReceiver(routeDirectionsRequestsService, new IntentFilter(RouteDirectionsRequestsService.ACTION_ROUTE_BROADCAST));
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
                    .unregisterReceiver(routeDirectionsRequestsService);
        }
        presenter.onDestroy();
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
            this.googleMap.setMyLocationEnabled(true);
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
        Polyline route = googleMap.addPolyline(routePolyOptions);
        mapsViewModel.routes.add(route);
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

    @Override
    public void showRouteReCalculationsDialog() {
        Context context = getContext();
        if (context != null) {
            String msg = getString(R.string.message_are_you_sure_you_want_to_recalculate);
            DialogUtils.getAlertDialogBuilder(context, msg, getString(R.string.title_route_recalculation))
                    .setPositiveButton(getString(R.string.btn_ok), (dialog, which) -> {
                        presenter.onRecalculateRoutesConfirmation();
                        dialog.dismiss();
                    })
                    .setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    public void openNavigationForSelectedMarker() {
        Marker navigateToMarker = mapsViewModel.currentSelectedMarker.get();
        if (navigateToMarker != null) {
            startMapsNavigationViaURL(navigateToMarker);
        } else {
            showMessage(getString(R.string.message_no_checkpoint_selected));
        }
    }

    /**
     * Starts the google maps Navigation Mode for the selected marker
     */
    private void startNavigation(@NonNull LatLng latLng) {
       String navUri = getString(R.string.format_navigation_directions, MapsFragmentView.OPEN_NAVIGATION_URI, latLng.latitude, latLng.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navUri));
        mapIntent.setPackage(MapsFragmentView.OPEN_NAVIGATION_MAPS_PACKAGE_NAME);
        startActivity(mapIntent);
    }

    /**
     * Starts the google maps Navigation Mode for the selected marker
     */
    private void startMapsNavigationViaURL(@NonNull Marker navigationOriginMarker) {
        presenter.onNavigationClicked(navigationOriginMarker);
    }

    @Override
    public void startGoogleMapsDirections(@NonNull String navigationUri) {
       //"https://www.google.com/maps/dir/?api=1&origin=22.553600,88.409969&destination=22.569272,88.406490&waypoints=22.558090,88.411363|22.561650,88.408066|22.561955,88.407858";
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

    public void calculateRoutesClicked(){
        presenter.onCalculateRoutesClicked();
    }

    public void onTotalRouteLengthClicked() {
        presenter.onTotalRouteInfoClicked();
    }

    public void onCalculateDistanceBetweenCheckpoints() {
        presenter.onCalculateDistanceBetweenTwoCheckpointsClicked();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_route_calculations:
                presenter.onStopCalculatingRoutesClicked();
                break;
        }
    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentPlanNewTripBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseMapFragment;
import com.grandtour.ev.evgrandtour.ui.currentTripView.CurrentTripFragmentView;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.TripCheckpointDetailsFragmentView;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class PlanNewTripFragmentView extends BaseMapFragment<PlanNewTripPresenter>
        implements PlanNewTripContract.View, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    @NonNull
    public static final String TAG = PlanNewTripFragmentView.class.getSimpleName();
    @NonNull
    private final PlanNewTripViewModel viewModel = new PlanNewTripViewModel();
    @Nullable
    private ClusterManager<TripCheckpoint> clusterManager;
    @NonNull
    private final ArrayList<Polyline> inPlanningTripRoute = new ArrayList<>();
    private FragmentPlanNewTripBinding viewBinding;

    @NonNull
    public static PlanNewTripFragmentView createInstance() {
        return new PlanNewTripFragmentView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_new_trip, container, false);
        viewBinding.setViewModel(viewModel);
        viewBinding.setPresenter(getPresenter());
        setMapView(viewBinding.mapViewAddTrip);
        initGoogleMapsView(savedInstanceState);
        return viewBinding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        Activity activity = getActivity();
        if (activity != null) {
            if (PermissionUtils.checkPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                setupGoogleMapsDarkStyle();
                setupClusterManager(activity);
                setupGoogleMapCallbacks();
                getPresenter().onMapReady();
            } else {
                PermissionUtils.requestPermissionsInFragment(this, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void setupGoogleMapCallbacks() {
        GoogleMap googleMap = getGoogleMap();
        if (googleMap != null && clusterManager != null) {
            googleMap.setOnMapClickListener(this);
            googleMap.setOnCameraIdleListener(clusterManager);
            googleMap.setOnMarkerClickListener(clusterManager);
            googleMap.setOnInfoWindowClickListener(clusterManager);
            googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
            googleMap.setOnInfoWindowCloseListener(this);
            googleMap.setOnMarkerDragListener(clusterManager.getMarkerManager());
        }
    }

    private void setupClusterManager(@NonNull Activity activity) {
        GoogleMap googleMap = getGoogleMap();
        if (googleMap != null) {
            clusterManager = new ClusterManager<>(activity, googleMap);
            clusterManager.setOnClusterClickListener(this);
            clusterManager.setOnClusterItemClickListener(this);
            clusterManager.setRenderer(new TripCheckpointsClusterRenderer(activity, googleMap, clusterManager));
            clusterManager.getMarkerCollection()
                    .setOnMarkerDragListener(this);
        }
    }

    @Nullable
    @Override
    protected PlanNewTripPresenter createPresenter() {
        return new PlanNewTripPresenter(this);
    }

    @Override
    public void showLoadingView(boolean isLoading) {
        viewModel.isLoadingInProgress.set(isLoading);
    }

    @Override
    public void displayNewTripCheckpointOnMap(@NonNull TripCheckpoint newCheckpoint) {
        if (clusterManager != null) {
            clusterManager.addItem(newCheckpoint);
            clusterManager.cluster();
        }
    }

    @Override
    public void displayPlannedTripCheckpointsOnMapView(@NonNull List<TripCheckpoint> savedCheckpoints) {
        if (clusterManager != null) {
            clusterManager.addItems(savedCheckpoints);
            clusterManager.cluster();
        }
    }

    @Override
    public void displayPlannedTripNameAndDescription(@NonNull String tripName, @NonNull String tripDescription) {
        viewModel.tripTitle.set(tripName);
    }

    @Override
    public void openNewCheckpointDetailsDialog(@NonNull LatLng clickedLocation) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TripCheckpointDetailsFragmentView.NEW_CHECKPOINT_LOCATION, clickedLocation);
        openEditTripCheckpointDetailsDialog(bundle);
    }

    @Override
    public void displayInvalidTripNameWarning() {
        viewModel.isCheckpointNameIncomplete.set(true);
    }

    @Override
    public void removeAddedTripCheckpoint(@NonNull TripCheckpoint tripCheckpoint) {
        if (clusterManager != null) {
            clusterManager.removeItem(tripCheckpoint);
            clusterManager.cluster();
        }
    }

    @Override
    public void displayTripCheckpointsInReorderingList(@NonNull List<TripCheckpoint> checkpointReorderingList) {
        viewModel.reorderingList.addAll(checkpointReorderingList);
    }

    @Override
    public void moveCameraToLocation(@NonNull LatLng latLng) {
        GoogleMap googleMap = getGoogleMap();
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CurrentTripFragmentView.ZOOM_LEVEL));
        }
    }

    @Override
    public void drawRoutePolyline(@NonNull PolylineOptions routePolyOptions) {
        Activity activity = getActivity();
        GoogleMap googleMap = getGoogleMap();
        if (activity != null && googleMap != null) {
            inPlanningTripRoute.add(googleMap.addPolyline(routePolyOptions));
        }
    }

    @Override
    public void clearRoutePolyline() {
        for (Polyline route : inPlanningTripRoute) {
            route.remove();
        }
        inPlanningTripRoute.clear();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
    }

    @Override
    public void onMapClick(LatLng latLng) {
        getPresenter().onMapLocationClicked(latLng);
    }

    @Override
    public void onCheckpointDetailsUpdated(@NonNull TripCheckpoint tripCheckpoint) {
        getPresenter().onNewTripCheckpointAdded(tripCheckpoint);
    }

    @Override
    public void onCheckpointDeleted(@NonNull TripCheckpoint tripCheckpoint) {
        getPresenter().onDeleteCheckpointFromTrip(tripCheckpoint);
    }

    @Override
    public boolean onClusterClick(Cluster<TripCheckpoint> cluster) {
        return false;
    }

    @Override
    public boolean onClusterItemClick(TripCheckpoint tripCheckpoint) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TripCheckpointDetailsFragmentView.PREVIOUS_TRIP_CHECKPOINT_DETAILS, tripCheckpoint);
        openEditTripCheckpointDetailsDialog(bundle);
        return false;
    }

    private void openEditTripCheckpointDetailsDialog(@NonNull Bundle bundle) {
        TripCheckpointDetailsFragmentView detailsFragmentView = TripCheckpointDetailsFragmentView.createInstance();
        detailsFragmentView.setArguments(bundle);
        showDialog(detailsFragmentView, this, TripCheckpointDetailsFragmentView.TAG, 112);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}

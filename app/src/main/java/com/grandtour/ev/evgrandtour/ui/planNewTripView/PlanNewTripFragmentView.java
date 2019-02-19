package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentPlanNewTripViewBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseMapFragment;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.TripCheckpointDetailsFragmentView;
import com.grandtour.ev.evgrandtour.ui.utils.PermissionUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlanNewTripFragmentView extends BaseMapFragment<PlanNewTripPresenter> implements PlanNewTripContract.View, GoogleMap.OnMapClickListener {

    @NonNull
    public static String TAG = PlanNewTripFragmentView.class.getSimpleName();
    @NonNull
    private final PlanNewTripViewModel viewModel = new PlanNewTripViewModel();
    @Nullable
    private ClusterManager<TripCheckpoint> clusterManager;

    @NonNull
    public static PlanNewTripFragmentView createInstance() {
        return new PlanNewTripFragmentView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPlanNewTripViewBinding viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_new_trip_view, container, false);
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
            } else {
                PermissionUtils.requestPermissionsInFragment(this, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void setupGoogleMapCallbacks() {
        GoogleMap googleMap = getGoogleMap();
        if (googleMap != null && clusterManager != null) {
            googleMap.setOnMapClickListener(this);
            googleMap.setOnCameraChangeListener(clusterManager);
            googleMap.setOnMarkerClickListener(clusterManager);
            googleMap.setOnInfoWindowClickListener(clusterManager);
            googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
            googleMap.setOnInfoWindowCloseListener(this);
        }
    }

    private void setupClusterManager(@NonNull Activity activity) {
        GoogleMap googleMap = getGoogleMap();
        if (googleMap != null) {
            clusterManager = new ClusterManager<>(activity, googleMap);
            clusterManager.setOnClusterClickListener(this);
            clusterManager.setOnClusterItemClickListener(this);
            clusterManager.setRenderer(new DefaultClusterRenderer<>(activity, googleMap, clusterManager));
        }
    }

    @Nullable
    @Override
    protected PlanNewTripPresenter createPresenter() {
        return new PlanNewTripPresenter(this);
    }

    @Override
    public void showLoadingView(boolean isLoading) {

    }

    @Override
    public void displayTripCheckpointOnMap(@NonNull TripCheckpoint newCheckpoint) {
        if (clusterManager != null) {
            clusterManager.addItem(newCheckpoint);
            clusterManager.cluster();
        }
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
    public void onCheckpointDeleted(int checkpointId) {
        getPresenter().onDeleteCheckpointFromTrip(checkpointId);
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
}

package com.grandtour.ev.evgrandtour.ui.addNewTrip;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentAddEditTripsBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseMapFragment;
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

import java.util.List;

public class AddNewTripFragmentView extends BaseMapFragment<AddNewTripPresenter> implements AddNewTripContract.View {

    @NonNull
    public static String TAG = AddNewTripFragmentView.class.getSimpleName();
    @NonNull
    private final AddNewTripViewModel viewModel = new AddNewTripViewModel();
    @Nullable
    private FragmentAddEditTripsBinding viewBinding;

    @NonNull
    public static AddNewTripFragmentView createInstance() {
        return new AddNewTripFragmentView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_edit_trips, container, false);
        viewBinding.setViewModel(viewModel);
        viewBinding.setPresenter(getPresenter());

        setMapView(viewBinding.mapViewAddTrip);
        initGoogleMapsView(savedInstanceState);
        return viewBinding.getRoot();
    }

    @Nullable
    @Override
    protected AddNewTripPresenter createPresenter() {
        return new AddNewTripPresenter(this);
    }

    @Override
    public void showLoadingView(boolean isLoading) {

    }

    @Override
    public void loadAllSavedTripCheckpoints(@NonNull List<Marker> checkpoints) {

    }

    @Override
    public void addNewCheckpointOnMap(@NonNull Marker newCheckpoint) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        Activity activity = getActivity();
        if (activity != null) {
            if (PermissionUtils.checkPermissions(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                setupGoogleMapsDarkStyle();
            } else {
                PermissionUtils.requestPermissionsInFragment(this, PermissionUtils.LOCATION_REQUEST_PERMISSION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }
}

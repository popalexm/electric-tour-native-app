package com.grandtour.ev.evgrandtour.ui.base;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseMapFragment<P extends BaseContract.Presenter> extends Fragment
        implements BaseContract.View, OnMapReadyCallback, GoogleMap.OnInfoWindowCloseListener {

    private P presenter;

    @Nullable
    private MapView mapView;
    @Nullable
    private GoogleMap googleMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getPresenter() != null) {
            presenter.onAttachView();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getPresenter() != null) {
            presenter.onDetachView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (getPresenter() != null) {
            presenter.onDestroyView();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    /**
     * Initialises the mapView, to be called from the on create method
     */
    protected void initGoogleMapsView(@Nullable Bundle savedInstanceState) {
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    /**
     * Setups google Maps into dark mode styling
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    protected void setupGoogleMapsDarkStyle() {
        if (googleMap != null) {
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
    }

    @Override
    public void showMessage(@NonNull String message) {
        Activity activity = getActivity();
        if (activity != null) {
            Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.toast_message_layout, null);
            TextView textView = view.findViewById(R.id.txtToastMessage);
            textView.setText(message);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    protected void showDialog(@NonNull DialogFragment dialogFrag, @NonNull Fragment targetFrag, @NonNull String tag, int requestCode) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        Fragment previousDialog = childFragmentManager.findFragmentByTag(tag);
        if (previousDialog != null) {
            fragmentTransaction.remove(previousDialog);
        }
        fragmentTransaction.addToBackStack(null);
        dialogFrag.setTargetFragment(targetFrag.getParentFragment(), requestCode);
        dialogFrag.show(fragmentTransaction, tag);
    }

    public P getPresenter() {
        if (presenter == null) {
            presenter = createPresenter();
        }
        return presenter;
    }

    @Nullable
    protected abstract P createPresenter();

    @Nullable
    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(@Nullable MapView mapView) {
        this.mapView = mapView;
    }

    @Nullable
    public GoogleMap getGoogleMap() {
        return googleMap;
    }
}

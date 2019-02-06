package com.grandtour.ev.evgrandtour.ui.addNewTrip;

import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentAddEditTripsBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseFragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AddNewTripFragmentView extends BaseFragment<AddNewTripPresenter> implements AddNewTripContract.View {

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
}

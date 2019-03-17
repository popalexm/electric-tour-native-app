package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentCreateNewTripBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class CreateNewTripFragment extends BaseFragment<CreateNewTripPresenter> implements CreateNewTripContract.View {

    @NonNull
    public final static String TAG = CreateNewTripFragment.class.getSimpleName();

    @NonNull
    private final CreateNewTripViewModel viewModel = new CreateNewTripViewModel();

    @NonNull
    public static CreateNewTripFragment createInstance() {
        return new CreateNewTripFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCreateNewTripBinding viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_new_trip, container, false);
        viewBinding.setViewModel(viewModel);
        viewBinding.setPresenter(getPresenter());
        return viewBinding.getRoot();
    }

    @Override
    public void showLoadingView(boolean isLoading) {
        viewModel.isLoadingInProgress.set(isLoading);
    }

    @Nullable
    @Override
    protected CreateNewTripPresenter createPresenter() {
        return new CreateNewTripPresenter(this);
    }
}

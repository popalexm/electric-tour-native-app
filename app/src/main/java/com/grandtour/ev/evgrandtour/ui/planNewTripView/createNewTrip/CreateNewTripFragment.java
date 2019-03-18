package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentCreateNewTripBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseFragment;
import com.grandtour.ev.evgrandtour.ui.mainActivity.NavigationFlowListener;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.PlanNewTripFragment;

import android.content.Context;
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
    public final static String BUNDLE_TRIP_ID = "BUNDLE_TRIP_ID";
    @NonNull
    private final CreateNewTripViewModel viewModel = new CreateNewTripViewModel();
    @Nullable
    private NavigationFlowListener navigationFlowListener;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            navigationFlowListener = (NavigationFlowListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass()
                    .getSimpleName() + " must implement NavigationFlowListener");
        }
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

    @Override
    public void displayErrorOnTripNameField(@NonNull String error) {
        viewModel.newTripNameErrorMessage.set(error);
    }

    @Override
    public void removeErrorOnTripNameField() {
        viewModel.newTripNameErrorMessage.set(null);
    }

    @Override
    public void moveToTripCheckpointsPlanningScreen(int tripId) {
        PlanNewTripFragment fragment = PlanNewTripFragment.createInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(CreateNewTripFragment.BUNDLE_TRIP_ID, tripId);
        fragment.setArguments(bundle);
        if (navigationFlowListener != null) {
            navigationFlowListener.moveToFragment(fragment, PlanNewTripFragment.TAG);
        }
    }
}

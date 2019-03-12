package com.grandtour.ev.evgrandtour.ui.chooseTour;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentDialogChooseTripBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;
import com.grandtour.ev.evgrandtour.ui.currentTripView.listeners.OnSelectedTourListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

public class ChooseTourDialogFragment extends BaseDialogFragment<ChooseTourDialogContract.Presenter> implements ChooseTourDialogContract.View {

    @NonNull
    public static final String TAG = ChooseTourDialogFragment.class.getSimpleName();
    @NonNull
    private final ChooseTourDialogViewModel viewModel = new ChooseTourDialogViewModel();

    @NonNull
    public static ChooseTourDialogFragment createInstance() {
        return new ChooseTourDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDialogChooseTripBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_choose_trip, null, false);
        binding.setViewModel(viewModel);
        binding.setPresenter(getPresenter());
        setupTransparentDialogBackground();
        return binding.getRoot();
    }

    @Override
    public ChooseTourDialogContract.Presenter createPresenter() {
        return new ChooseTourDialogPresenter(this);
    }

    @Override
    public void showLoadingView(boolean isLoading) {
        viewModel.isLoadingInProgress.set(isLoading);
    }

    @Override
    public void loadAvailableTours(@NonNull List<TourModel> tours) {
        viewModel.availableTours.clear();
        viewModel.availableTours.addAll(tours);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    @Override
    public void saveSelectionAndDismiss(@NonNull String tourId) {
        OnSelectedTourListener callback = (OnSelectedTourListener) getParentFragment();
        if (callback != null) {
            getPresenter().OnSelectionSaved(callback, tourId);
        }
        dismiss();
    }
}

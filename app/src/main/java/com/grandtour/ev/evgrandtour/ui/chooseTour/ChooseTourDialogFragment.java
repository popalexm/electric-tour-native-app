package com.grandtour.ev.evgrandtour.ui.chooseTour;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentDialogRoutesBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.listeners.OnSelectedTourListener;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class ChooseTourDialogFragment extends BaseDialogFragment implements ChooseTourDialogContract.View {

    @NonNull
    public static final String TAG = ChooseTourDialogFragment.class.getSimpleName();
    @NonNull
    private final ChooseTourDialogPresenter presenter = new ChooseTourDialogPresenter(this);
    @NonNull
    private final ChooseTourDialogViewModel viewModel = new ChooseTourDialogViewModel();

    @NonNull
    public static ChooseTourDialogFragment createInstance() {
        return new ChooseTourDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDialogRoutesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_routes, null, false);
        binding.setViewModel(viewModel);
        binding.setPresenter(presenter);
        setupTransparentDialogBackground();
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter.onAttachView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetachView();
    }

    @Override
    public void showLoadingView(boolean isLoading) {
        viewModel.isLoadingInProgress.set(isLoading);
    }

    @Override
    public void showMessage(@NonNull String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                .show();
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
            presenter.OnSelectionSaved(callback, tourId);
        }
        dismiss();
    }
}

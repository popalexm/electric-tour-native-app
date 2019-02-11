package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentDialogTripCheckpointDetailsViewBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewTripCheckpointDetailsFragmentView extends BaseDialogFragment<NewTripCheckpointDetailsFragmentPresenter>
        implements NewTripCheckpointDetailsFragmentContract.View {

    @NonNull
    public final static String TAG = NewTripCheckpointDetailsFragmentView.class.getSimpleName();
    @NonNull
    private final NewTripCheckpointDetailsViewModel viewModel = new NewTripCheckpointDetailsViewModel();

    @NonNull
    public static NewTripCheckpointDetailsFragmentView createInstance() {
        return new NewTripCheckpointDetailsFragmentView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDialogTripCheckpointDetailsViewBinding viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_trip_checkpoint_details_view,
                container, false);
        viewBinding.setPresenter(getPresenter());
        viewBinding.setViewModel(viewModel);
        setupTransparentDialogBackground();
        return viewBinding.getRoot();
    }

    @Nullable
    @Override
    public NewTripCheckpointDetailsFragmentPresenter createPresenter() {
        return new NewTripCheckpointDetailsFragmentPresenter(this);
    }

    @Override
    public void showLoadingView(boolean isLoading) {

    }

    @Override
    public void dismissDetailsDialog() {
        dismiss();
    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentDialogTripCheckpointDetailsViewBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

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
    public final static String TRIP_CHECKPOINT_DETAILS = "trip_checkpoint_details";

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
        initParentFragmentCallback();
        retrieveCheckpointDetails(getArguments());
        return viewBinding.getRoot();
    }

    /**
     * Send a reference to the callback that will be used to save the checkpoint details in te parent fragment
     */
    private void initParentFragmentCallback() {
        AddNewCheckpointDetailsCallback callback = (AddNewCheckpointDetailsCallback) getParentFragment();
        if (callback != null) {
            getPresenter().onInitCallbackToParentFragment(callback);
        }
    }

    /**
     * Retrieves trip checkpoint details from the parcel
     */
    private void retrieveCheckpointDetails(@Nullable Bundle bundle) {
        if (bundle != null) {
            TripCheckpoint tripCheckpoint = bundle.getParcelable(NewTripCheckpointDetailsFragmentView.TRIP_CHECKPOINT_DETAILS);
            if (tripCheckpoint != null) {
                getPresenter().onRetrievedTripCheckpointDetailsFromBundle(tripCheckpoint);
            }
        }
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

    @Override
    public void shakeCheckpointNameTextView() {
        viewModel.isCheckpointNameIncomplete.set(true);
    }
}

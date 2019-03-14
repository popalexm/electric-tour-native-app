package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.google.android.gms.maps.model.LatLng;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.FragmentDialogEditCheckpointBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails.callbacks.CheckpointDetailsCallback;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

public class TripCheckpointDetailsFragmentView extends BaseDialogFragment<TripCheckpointDetailsFragmentPresenter>
        implements TripCheckpointDetailsFragmentContract.View {

    @NonNull
    public final static String TAG = TripCheckpointDetailsFragmentView.class.getSimpleName();
    @NonNull
    public final static String PREVIOUS_TRIP_CHECKPOINT_DETAILS = "previous_trip_checkpoint_details";
    @NonNull
    public final static String NEW_CHECKPOINT_LOCATION = "new_trip_checkpoint_details";

    @NonNull
    private final TripCheckpointDetailsViewModel viewModel = new TripCheckpointDetailsViewModel();

    @NonNull
    public static TripCheckpointDetailsFragmentView createInstance() {
        return new TripCheckpointDetailsFragmentView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentDialogEditCheckpointBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_dialog_edit_checkpoint,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(viewBinding.getRoot());
        viewBinding.setPresenter(getPresenter());
        viewBinding.setViewModel(viewModel);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        initParentFragmentCallback();
        retrieveCheckpointDetails(getArguments());
    }

    /**
     * Send a reference to the callback that will be used to save the checkpoint details in te parent fragment
     */
    private void initParentFragmentCallback() {
        CheckpointDetailsCallback callback = (CheckpointDetailsCallback) getParentFragment();
        if (callback != null) {
            getPresenter().onInitCallbackToParentFragment(callback);
        }
    }

    /**
     * Retrieves trip checkpoint details from the parcel
     */
    private void retrieveCheckpointDetails(@Nullable Bundle bundle) {
        if (bundle != null) {
            TripCheckpoint tripCheckpoint = bundle.getParcelable(TripCheckpointDetailsFragmentView.PREVIOUS_TRIP_CHECKPOINT_DETAILS);
            if (tripCheckpoint != null) {
                getPresenter().onRetrievedTripCheckpointDetailsFromBundle(tripCheckpoint);
            } else {
                LatLng newCheckpointLocation = bundle.getParcelable(TripCheckpointDetailsFragmentView.NEW_CHECKPOINT_LOCATION);
                if (newCheckpointLocation != null) {
                    getPresenter().onNewCheckpointDetailsInitialised(newCheckpointLocation);
                }
            }
        }
    }

    @Nullable
    @Override
    public TripCheckpointDetailsFragmentPresenter createPresenter() {
        return new TripCheckpointDetailsFragmentPresenter(this);
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

    @Override
    public void displaySavedCheckpointDetails(@NonNull TripCheckpoint tripCheckpoint) {
        viewModel.checkpointName.set(tripCheckpoint.getCheckpointTitle());
        viewModel.checkpointDescription.set(tripCheckpoint.getCheckpointDescription());
        viewModel.checkpointAddress.set(tripCheckpoint.getCheckpointAddress());
    }

    @Override
    public void displaySearchedAddressForCheckpoint(@NonNull String address) {
        viewModel.checkpointAddress.set(address);
    }

    @Override
    public void displayDeleteButton(boolean shouldDeleteButtonBeDisplayed) {
        viewModel.isCheckpointInEditMode.set(shouldDeleteButtonBeDisplayed);
    }
}

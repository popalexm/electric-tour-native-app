package com.grandtour.ev.evgrandtour.ui.distancePicker;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.databinding.FragmentDistancePickerBinding;
import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.base.BottomDialogFragment;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

public class DistancePickerDialogFragment extends BottomDialogFragment implements DistancePickerContract.View {

    @NonNull
    public final static String TAG = DistancePickerDialogFragment.class.getSimpleName();
    @NonNull
    public final DistancePickerViewModel distancePickerViewModel = new DistancePickerViewModel();
    @NonNull
    public final DistancePickerPresenter presenter = new DistancePickerPresenter(this);
    @Nullable
    private FragmentDistancePickerBinding binding;

    public void setTotalCheckpoints(@NonNull Collection<Checkpoint> totalCheckpoints) {
        Collection<DistancePointViewModel> viewModels = new ArrayList<>();
        for (Checkpoint checkpoint : totalCheckpoints) {
            String displayedName = checkpoint.getOrderInTourId() + "   " + checkpoint.getCheckpointName();
            viewModels.add(new DistancePointViewModel(checkpoint.getCheckpointId(), displayedName));
        }
        this.distancePickerViewModel.totalCheckpoints.addAll(viewModels);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_distance_picker, null, false);
        binding.setViewModel(distancePickerViewModel);
        binding.setPresenter(presenter);
        setupTransparentDialogBackground();
        setupBottomSheetToExpanded();
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter.onAttach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }

    @Override
    public void displayDistance(@NonNull Integer distance, @NonNull String duration) {
        Activity activity = getActivity();
        if (activity != null && binding != null) {
            String dist = getString(R.string.format_distance_between, distance, getString(R.string.suffix_kilometers), duration);
            binding.distance.setTextColor(activity.getResources()
                    .getColor(R.color.colorLightGrey));
            distancePickerViewModel.calculatedDistance.set(dist);
        }
    }

    @Override
    public void calculateDistances() {
        int startId = distancePickerViewModel.selectedStartCheckpoint.get();
        int endId = distancePickerViewModel.selectedEndCheckpoint.get();
        presenter.onCalculateRouteInformationClicked(distancePickerViewModel.totalCheckpoints.get(startId).checkpointId,
                distancePickerViewModel.totalCheckpoints.get(endId).checkpointId);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    @Override
    public void showLoadingView(boolean isLoading) {
    }

    @Override
    public void showMessage(@NonNull String msg) {
        Activity activity = getActivity();
        if (binding != null && activity != null) {
            distancePickerViewModel.calculatedDistance.set(msg);
            binding.distance.setTextColor(Injection.provideGlobalContext()
                    .getResources()
                    .getColor(R.color.colorRed));
            AnimationManager.getInstance()
                    .shakeTextView(binding.distance);
        }
    }
}

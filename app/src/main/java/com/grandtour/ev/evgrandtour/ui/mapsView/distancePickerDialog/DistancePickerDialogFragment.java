package com.grandtour.ev.evgrandtour.ui.mapsView.distancePickerDialog;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.data.database.models.Checkpoint;
import com.grandtour.ev.evgrandtour.databinding.FragmentDistancePickerBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collection;

public class DistancePickerDialogFragment extends BaseDialogFragment implements DistancePickerContract.View {

    @NonNull
    public final static String TAG = DistancePickerDialogFragment.class.getSimpleName();
    @NonNull
    public final DistancePickerViewModel distancePickerViewModel = new DistancePickerViewModel();
    @NonNull
    public final DistancePickerPresenter presenter = new DistancePickerPresenter(this);

    public void setTotalCheckpoints(@NonNull Collection<Checkpoint> totalCheckpoints) {
        this.distancePickerViewModel.totalCheckpoints.addAll(totalCheckpoints);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDistancePickerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_distance_picker, null, false);
        binding.setViewModel(distancePickerViewModel);
        binding.setPresenter(presenter);
        setupTransparentDialogBackground();
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
        String dist = getString(R.string.format_distance_between, distance, getString(R.string.suffix_kilometers), duration);
        distancePickerViewModel.calculatedDistance.set(dist);
    }

    @Override
    public void calculateDistances() {
        int startId = distancePickerViewModel.selectedStartCheckpoint.get();
        int endId = distancePickerViewModel.selectedEndCheckpoint.get();
        presenter.onCalculateRouteInformationClicked(distancePickerViewModel.totalCheckpoints.get(startId)
                .getCheckpointId(), distancePickerViewModel.totalCheckpoints.get(endId)
                .getCheckpointId());
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                .show();
    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

public class PlanNewTripViewModel {

    @NonNull
    public final ObservableBoolean isCheckpointNameIncomplete = new ObservableBoolean();
    @NonNull
    public ObservableField<String> tripTitle = new ObservableField<>("");
    @NonNull
    public ObservableList<TripCheckpoint> reorderingList = new ObservableArrayList<>();
    @NonNull
    public ObservableBoolean isLoadingInProgress = new ObservableBoolean(false);

   /* @NonNull
    public ItemBinding<TripCheckpoint> itemBindingReorderingList = ItemBinding.of(BR.viewModel, R.layout.item_reorder_trip_checkpoint); */

}

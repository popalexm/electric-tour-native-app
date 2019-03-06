package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

public class PlanNewTripViewModel {

    @NonNull
    public final ObservableBoolean isCheckpointNameIncomplete = new ObservableBoolean();
    @NonNull
    public ObservableField<String> tripTitle = new ObservableField<>("");
    @NonNull
    public ObservableList<TripCheckpoint> reorderingList = new ObservableArrayList<>();
   /* @NonNull
    public ItemBinding<TripCheckpoint> itemBindingReorderingList = ItemBinding.of(BR.viewModel, R.layout.item_reorder_trip_checkpoint); */

}

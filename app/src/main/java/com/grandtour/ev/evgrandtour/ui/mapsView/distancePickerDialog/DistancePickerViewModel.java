package com.grandtour.ev.evgrandtour.ui.mapsView.distancePickerDialog;

import com.grandtour.ev.evgrandtour.BR;
import com.grandtour.ev.evgrandtour.R;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class DistancePickerViewModel {

    @NonNull
    public final ObservableArrayList<DistancePointViewModel> totalCheckpoints = new ObservableArrayList<>();
    @NonNull
    public ItemBinding<DistancePointViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_spinner_layout);
    @NonNull
    public final ObservableInt selectedStartCheckpoint = new ObservableInt();
    @NonNull
    public final ObservableInt selectedEndCheckpoint = new ObservableInt();
    @NonNull
    public final ObservableField<String> calculatedDistance = new ObservableField<>("");

}

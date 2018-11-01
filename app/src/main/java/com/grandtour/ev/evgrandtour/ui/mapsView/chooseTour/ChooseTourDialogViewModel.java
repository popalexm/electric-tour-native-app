package com.grandtour.ev.evgrandtour.ui.mapsView.chooseTour;

import com.grandtour.ev.evgrandtour.BR;
import com.grandtour.ev.evgrandtour.R;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ChooseTourDialogViewModel {

    @NonNull
    public ItemBinding<TourModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_route_selection);
    @NonNull
    public ObservableList<TourModel> availableTours = new ObservableArrayList<>();
    @NonNull
    public ObservableBoolean isLoadingInProgress = new ObservableBoolean();

}

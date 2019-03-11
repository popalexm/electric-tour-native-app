package com.grandtour.ev.evgrandtour.ui.chooseTour;

import com.grandtour.ev.evgrandtour.BR;
import com.grandtour.ev.evgrandtour.R;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ChooseTourDialogViewModel {

    @NonNull
    public ItemBinding<TourModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_route_selection);
    @NonNull
    public ObservableList<TourModel> availableTours = new ObservableArrayList<>();
    @NonNull
    public ObservableBoolean isLoadingInProgress = new ObservableBoolean();

}

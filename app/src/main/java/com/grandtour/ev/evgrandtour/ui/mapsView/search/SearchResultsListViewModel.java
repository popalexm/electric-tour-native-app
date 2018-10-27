package com.grandtour.ev.evgrandtour.ui.mapsView.search;

import com.android.databinding.library.baseAdapters.BR;
import com.grandtour.ev.evgrandtour.R;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class SearchResultsListViewModel {

    @NonNull
    public final ItemBinding<SearchResultViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_search_view);
    @NonNull
    public final ObservableList<SearchResultViewModel> parameters = new ObservableArrayList<>();

}

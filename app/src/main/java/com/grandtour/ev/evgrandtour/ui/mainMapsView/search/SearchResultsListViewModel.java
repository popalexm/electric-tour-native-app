package com.grandtour.ev.evgrandtour.ui.mainMapsView.search;

import com.android.databinding.library.baseAdapters.BR;
import com.grandtour.ev.evgrandtour.R;

import android.support.annotation.NonNull;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList;

public class SearchResultsListViewModel {

    @NonNull
    public final ItemBinding<SearchResultViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_search_view);
    @NonNull
    public final DiffObservableList<SearchResultViewModel> parameters = new DiffObservableList(new DiffObservableList.Callback<SearchResultViewModel>() {
        @Override
        public boolean areItemsTheSame(SearchResultViewModel oldItem, SearchResultViewModel newItem) {
            return oldItem.searchResultId.equals(newItem.searchResultId);
        }

        @Override
        public boolean areContentsTheSame(SearchResultViewModel oldItem, SearchResultViewModel newItem) {
            return oldItem.searchResultId.equals(newItem.searchResultId);
        }
    });


}

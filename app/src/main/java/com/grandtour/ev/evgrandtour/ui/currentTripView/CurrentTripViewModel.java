package com.grandtour.ev.evgrandtour.ui.currentTripView;

import com.google.android.material.chip.Chip;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.currentTripView.models.SearchResultModel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList;

public class CurrentTripViewModel {

    @NonNull
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isSearchViewOpen = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isWarningState = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isSelectTourButtonDisplayed = new ObservableBoolean(true);
    @NonNull
    public final ObservableBoolean isButtonBouncing = new ObservableBoolean(false);
    @NonNull
    public final ObservableField<String> routeTitle = new ObservableField<>("");
    @NonNull
    public final ObservableField<String> routeDrivingDistance = new ObservableField<>("");
    @NonNull
    public final ObservableField<String> routeDrivingDuration = new ObservableField<>("");
    @NonNull
    public final ObservableBoolean isFilteringLayoutVisible = new ObservableBoolean(false);
    @NonNull
    public final ObservableBoolean removeFilteringOptions = new ObservableBoolean(false);
    @NonNull
    public final ObservableArrayList<Chip> checkPointFilteringOptions = new ObservableArrayList<>();
    @NonNull
    public final ObservableBoolean areNavigationButtonsEnabled = new ObservableBoolean();
    @NonNull
    public final ItemBinding<SearchResultModel> resultViewModelItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_search_view);
    @SuppressWarnings("unchecked")
    @NonNull
    public final DiffObservableList<SearchResultModel> searchResultModels = new DiffObservableList(new DiffObservableList.Callback<SearchResultModel>() {
        @Override
        public boolean areItemsTheSame(SearchResultModel oldItem, SearchResultModel newItem) {
            return oldItem.searchResultId.equals(newItem.searchResultId);
        }

        @Override
        public boolean areContentsTheSame(SearchResultModel oldItem, SearchResultModel newItem) {
            return oldItem.searchResultId.equals(newItem.searchResultId);
        }
    });
}

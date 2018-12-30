package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import com.android.databinding.library.baseAdapters.BR;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.CurrentUserLocation;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.SearchResultModel;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList;

public class MapsViewModel {

    @NonNull
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    @NonNull
    public final ObservableArrayList<Polyline> routePolyLine = new ObservableArrayList<>();
    @NonNull
    public final ObservableArrayList<MapCheckpoint> routeCheckpoints = new ObservableArrayList<>();
    @NonNull
    public final ObservableField<CurrentUserLocation> currentUserLocation = new ObservableField<>();
    @NonNull
    public final ObservableField<Marker> currentSelectedMarker = new ObservableField<>();
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
    public final ItemBinding<SearchResultModel> resultViewModelItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_search_view);
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

    @BindingAdapter("isWarningState")
    public static void setViewStateAsWarning(View view, boolean isWarning) {
        if (isWarning) {
            AnimationManager.getInstance()
                    .shakeTextView(view);
        } else {
            view.setAnimation(null);
        }
    }

    @BindingAdapter("isButtonRevealed")
    public static void setRevealButtonState(@NonNull View view, boolean shouldButtonBeRevealed) {
        AnimationManager animationManager = AnimationManager.getInstance();
        if (shouldButtonBeRevealed) {
            animationManager.revealButtonAnimation(view);
        } else {
            animationManager.hideButtonAnimation(view);
        }
    }

    @BindingAdapter("isButtonBounced")
    public static void setBounceAnimation(@NonNull View view, boolean shouldButtonBeBounced) {
        AnimationManager animationManager = AnimationManager.getInstance();
        if (shouldButtonBeBounced) {
            animationManager.startBounceAnimation(view);
        }
    }

    @BindingAdapter("areFilteringOptionsDisplayed")
    public static void setRevealHideFilteringOptions(@NonNull View view, boolean areFilteringOptionsVisible) {
        AnimationManager animationManager = AnimationManager.getInstance();
        if (areFilteringOptionsVisible) {
            animationManager.slideAnimationDown(view);
            view.setVisibility(View.VISIBLE);
        } else {
            animationManager.slideAnimationUp(view);
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("toggleButtonState")
    public static void setButtonState(@NonNull MaterialButton btnFilter, boolean isFilteringLayoutVisible) {
        Context context = Injection.provideGlobalContext();
        if (isFilteringLayoutVisible) {
            btnFilter.setIcon(context.getResources()
                    .getDrawable(R.drawable.ic_clear_white_24dp));
            btnFilter.setBackgroundTintList(ColorStateList.valueOf(context.getResources()
                    .getColor(R.color.colorRed)));
            btnFilter.setText(context.getResources()
                    .getText(R.string.btn_clear));
        } else {
            btnFilter.setIcon(context.getResources()
                    .getDrawable(R.drawable.ic_filter_list_white_24dp));
            btnFilter.setBackgroundTintList(ColorStateList.valueOf(context.getResources()
                    .getColor(R.color.colorPrimaryDark)));
            btnFilter.setText(context.getResources()
                    .getText(R.string.btn_filter));
        }
    }

    @BindingAdapter("chipGroupItems")
    public static void setChipGroupItems(@NonNull ChipGroup chipGroup, @NonNull Iterable<Chip> chipArrayList) {
        for (Chip chip : chipArrayList) {
            chipGroup.addView(chip);
        }
    }

    @BindingAdapter("removeChipGroupItems")
    public static void removeChipGroupItems(@NonNull ChipGroup chipGroup, boolean shouldRemoveFilteringOptions) {
        if (shouldRemoveFilteringOptions) {
            chipGroup.removeAllViews();
        }
    }

    @BindingAdapter("areSearchResultsVisible")
    public static void setRecyclerViewVisibility(@NonNull RecyclerView recyclerView, List<SearchResultModel> searchResultModels) {
        if (searchResultModels.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }
}

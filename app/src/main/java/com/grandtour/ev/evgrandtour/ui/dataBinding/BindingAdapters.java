package com.grandtour.ev.evgrandtour.ui.dataBinding;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.currentTripView.listeners.OnQueryTextChangeListener;
import com.grandtour.ev.evgrandtour.ui.currentTripView.listeners.OnQueryTextSubmitListener;
import com.grandtour.ev.evgrandtour.ui.currentTripView.listeners.OnSearchViewCloseListener;
import com.grandtour.ev.evgrandtour.ui.currentTripView.models.SearchResultModel;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

import java.util.List;

public final class BindingAdapters {

    private BindingAdapters() {
    }

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
            view.setVisibility(View.VISIBLE);
            animationManager.revealButtonAnimation(view);
        } else {
            animationManager.hideButtonAnimation(view);
            view.setVisibility(View.GONE);
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

    @BindingAdapter(value = {"android:onQueryTextSubmit", "android:onQueryTextChange"}, requireAll = false)
    public static void setOnQueryTextListener(SearchView view, final OnQueryTextSubmitListener submit, final OnQueryTextChangeListener change) {
        if (submit == null && change == null) {
            view.setOnQueryTextListener(null);
        } else {
            view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (submit != null) {
                        return submit.onQueryTextSubmit(query);
                    } else {
                        return false;
                    }
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (change != null) {
                        return change.onQueryTextChange(newText);
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    @BindingAdapter(value = {"android:onSearchViewClosed"})
    public static void setOnSearchViewClosed(@NonNull SearchView searchView, final OnSearchViewCloseListener closeListener) {
        if (closeListener == null) {
            searchView.setOnCloseListener(null);
        } else {
            searchView.setOnCloseListener(() -> closeListener.onSearchViewClosed(true));
        }
    }

    @BindingAdapter(value = {"android:onSearchViewOpen"})
    public static void setOnSearchViewOpen(@NonNull SearchView searchView, final View.OnClickListener onSearchViewOpenListener) {
        if (onSearchViewOpenListener == null) {
            searchView.setOnSearchClickListener(null);
        } else {
            searchView.setOnSearchClickListener(onSearchViewOpenListener);
        }
    }
}

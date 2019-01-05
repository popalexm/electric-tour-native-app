package com.grandtour.ev.evgrandtour.ui.mainMapsView.models;

import com.grandtour.ev.evgrandtour.ui.mainMapsView.listeners.OnSearchResultClickListener;

import android.support.annotation.NonNull;

public class SearchResultModel {

    @NonNull
    public final Integer searchResultId;
    @NonNull
    public final String searchResultTourId;
    @NonNull
    public final String searchResultName;
    @NonNull
    public final OnSearchResultClickListener clickListener;

    public SearchResultModel(@NonNull Integer searchResultId, @NonNull String searchResultTourId, @NonNull String searchResultName,
            @NonNull OnSearchResultClickListener clickListener) {
        this.searchResultId = searchResultId;
        this.searchResultTourId = searchResultTourId;
        this.searchResultName = searchResultName;
        this.clickListener = clickListener;
    }
}

package com.grandtour.ev.evgrandtour.ui.mainMapsView.search;

import android.support.annotation.NonNull;

public class SearchResultViewModel {

    @NonNull
    public final Integer searchResultId;
    @NonNull
    public final String searchResultTourId;
    @NonNull
    public final String searchResultName;
    @NonNull
    public final SearchViewResultClickListener clickListener;

    public SearchResultViewModel(@NonNull Integer searchResultId, @NonNull String searchResultTourId, @NonNull String searchResultName,
            @NonNull SearchViewResultClickListener clickListener) {
        this.searchResultId = searchResultId;
        this.searchResultTourId = searchResultTourId;
        this.searchResultName = searchResultName;
        this.clickListener = clickListener;
    }
}

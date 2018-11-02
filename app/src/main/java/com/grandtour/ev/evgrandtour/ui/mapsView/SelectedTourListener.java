package com.grandtour.ev.evgrandtour.ui.mapsView;

import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;

import android.support.annotation.NonNull;

import java.util.List;

public interface SelectedTourListener {

    void OnSelectedTour(@NonNull String tourId, List<TourDataResponse> tourDataResponses);
}

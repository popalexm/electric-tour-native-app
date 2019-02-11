package com.grandtour.ev.evgrandtour.ui.currentTripView.listeners;

import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;

import android.support.annotation.NonNull;

import java.util.List;

public interface OnSelectedTourListener {

    void OnSelectedTour(@NonNull String tourId, List<TourDataResponse> tourDataResponses);
}

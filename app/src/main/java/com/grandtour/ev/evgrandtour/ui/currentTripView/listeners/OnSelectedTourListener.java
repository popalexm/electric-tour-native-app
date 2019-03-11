package com.grandtour.ev.evgrandtour.ui.currentTripView.listeners;

import com.grandtour.ev.evgrandtour.data.network.models.response.dailyTour.TourDataResponse;

import java.util.List;

import androidx.annotation.NonNull;

public interface OnSelectedTourListener {

    void OnSelectedTour(@NonNull String tourId, List<TourDataResponse> tourDataResponses);
}

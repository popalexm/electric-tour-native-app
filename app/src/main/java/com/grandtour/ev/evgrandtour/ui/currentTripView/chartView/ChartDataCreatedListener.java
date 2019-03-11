package com.grandtour.ev.evgrandtour.ui.currentTripView.chartView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;

import androidx.annotation.NonNull;

public interface ChartDataCreatedListener {

    void OnChartDataCreated(@NonNull LineData lineData, @NonNull Description description);

}

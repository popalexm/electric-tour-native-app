package com.grandtour.ev.evgrandtour.ui.mainMapsView.chartView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.LineData;

import android.support.annotation.NonNull;

public interface ChartDataCreatedListener {

    void OnChartDataCreated(@NonNull LineData lineData, @NonNull Description description);

}

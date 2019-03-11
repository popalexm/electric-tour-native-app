package com.grandtour.ev.evgrandtour.ui.currentTripView.chartView;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;

public class YAxisValueFormatter implements IAxisValueFormatter {

    @NonNull
    private final DecimalFormat mFormat = new DecimalFormat("###,###,###");
    private final static float KILOMETER_VALUE = 1000;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value >= YAxisValueFormatter.KILOMETER_VALUE) {
            value = value / YAxisValueFormatter.KILOMETER_VALUE;
            return mFormat.format(value) + " Km";
        } else {
            return mFormat.format(value) + " m";
        }
    }
}

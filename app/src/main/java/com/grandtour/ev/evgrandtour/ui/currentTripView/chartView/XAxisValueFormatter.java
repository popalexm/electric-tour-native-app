package com.grandtour.ev.evgrandtour.ui.currentTripView.chartView;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;

public class XAxisValueFormatter implements IAxisValueFormatter {

    @NonNull
    private final DecimalFormat mFormat = new DecimalFormat("#");

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value);
    }
}

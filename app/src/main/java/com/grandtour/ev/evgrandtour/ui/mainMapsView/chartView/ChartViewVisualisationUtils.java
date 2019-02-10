package com.grandtour.ev.evgrandtour.ui.mainMapsView.chartView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import android.graphics.Color;
import android.support.annotation.NonNull;

public final class ChartViewVisualisationUtils {

    private ChartViewVisualisationUtils() {
    }

    public static void showChartView(@NonNull LineChart chartView, @NonNull LineData lineData, @NonNull Description description) {
        chartView.setDescription(description);
        chartView.setData(lineData);
        chartView.getLegend()
                .setTextColor(Color.WHITE);
        chartView.invalidate();
        chartView.animateY(1000);
    }

    public static void setupChartViewAxisStyling(@NonNull LineChart chartView) {
        XAxis xAxis = chartView.getXAxis();
        YAxis yAxisLeft = chartView.getAxisLeft();
        YAxis yAxisRight = chartView.getAxisRight();

        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setValueFormatter(new YAxisValueFormatter());
        yAxisRight.setEnabled(false);

        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new XAxisValueFormatter());
    }
}

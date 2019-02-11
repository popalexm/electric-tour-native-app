package com.grandtour.ev.evgrandtour.ui.currentTripView.chartView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.database.models.ElevationPoint;

import android.graphics.Color;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ChartViewDataHandler {

    @NonNull
    private final List<ElevationPoint> elevationPoints;
    @NonNull
    private final ChartDataCreatedListener dataCreatedListener;
    @NonNull
    private final List<Entry> chartEntryPoints = new ArrayList<>();

    public ChartViewDataHandler(@NonNull List<ElevationPoint> elevationPoints, @NonNull ChartDataCreatedListener dataCreatedListener) {
        this.elevationPoints = elevationPoints;
        this.dataCreatedListener = dataCreatedListener;
        initChartEntries();
        createChartData();
    }

    @MainThread
    private void initChartEntries() {
        for (int i = 0; i < elevationPoints.size(); i++) {
            ElevationPoint elevationPoint = elevationPoints.get(i);
            int startCheckpointId = elevationPoint.getStartCheckpointOrderId();
            chartEntryPoints.add(new Entry(startCheckpointId, (float) elevationPoint.getElevation()));
        }
    }

    @MainThread
    private void createChartData() {
        LineDataSet lineDataSet = getChartLineDataSet(chartEntryPoints);
        LineData lineData = new LineData(lineDataSet);
        Description description = getChartDescription();
        dataCreatedListener.OnChartDataCreated(lineData, description);
    }

    @NonNull
    private LineDataSet getChartLineDataSet(@NonNull List<Entry> elevationPointsList) {
        int accentColor = Injection.provideResources()
                .getColor(R.color.colorAccent);
        String lineLabel = Injection.provideResources()
                .getString(R.string.label_line_chart);

        LineDataSet dataSet = new LineDataSet(elevationPointsList, lineLabel);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setColor(accentColor);
        dataSet.setCircleColor(accentColor);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawCircles(false);
        return dataSet;
    }

    @NonNull
    private Description getChartDescription() {
        String chartDescription = Injection.provideResources()
                .getString(R.string.label_line_chart_values);
        int descriptionLabelColor = Injection.provideResources()
                .getColor(R.color.colorLightGrey);

        Description description = new Description();
        description.setText(chartDescription);
        description.setTextColor(descriptionLabelColor);
        return description;
    }
}

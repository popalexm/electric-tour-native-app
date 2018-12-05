package com.grandtour.ev.evgrandtour.ui.elevationView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.databinding.FragmentElevationChartBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseBottomDialogFragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ElevationChartFragment extends BaseBottomDialogFragment implements ElevationChartContract.View {

    @NonNull
    private static final String BUNDLE_ROUTE_LEG_ID = "routeLegId";
    @NonNull
    public static final String TAG = ElevationChartFragment.class.getSimpleName();
    @NonNull
    private final ElevationChartPresenter presenter = new ElevationChartPresenter(this);
    @Nullable
    private Integer routeLegId;
    private FragmentElevationChartBinding viewBinding;

    public static ElevationChartFragment newChartForRouteLegInstance(int routeLegId) {
        ElevationChartFragment elevationInfoFragment = new ElevationChartFragment();
        Bundle args = new Bundle();
        args.putInt(ElevationChartFragment.BUNDLE_ROUTE_LEG_ID, routeLegId);
        elevationInfoFragment.setArguments(args);
        return elevationInfoFragment;
    }

    public static ElevationChartFragment newChartForEntireRouteInstance() {
        return new ElevationChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            routeLegId = bundle.getInt(ElevationChartFragment.BUNDLE_ROUTE_LEG_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_elevation_chart, null, false);
        viewBinding.setPresenter(presenter);
        return viewBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter.onAttach();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (routeLegId != null) {
            presenter.onDisplayElevationChartForRouteLeg(routeLegId);
        } else {
            presenter.onDisplayElevationChartForEntireTour();
        }
    }

    @Override
    public void displayChart(@NonNull List<Entry> elevationPointsList) {
        if (elevationPointsList.size() > 0) {
            int labelColor = Injection.provideGlobalContext()
                    .getResources()
                    .getColor(R.color.colorLightGrey);
            int accentColor = Injection.provideGlobalContext()
                    .getResources()
                    .getColor(R.color.colorAccent);

            String lineLabel = Injection.provideGlobalContext()
                    .getResources()
                    .getString(R.string.label_line_chart);
            String lineChartDescription = Injection.provideGlobalContext()
                    .getResources()
                    .getString(R.string.label_line_chart_values);

            LineDataSet dataSet = new LineDataSet(elevationPointsList, lineLabel);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setColor(accentColor);
            dataSet.setCircleColor(accentColor);

            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawCircles(false);

            LineData lineData = new LineData(dataSet);

            Description description = new Description();
            description.setText(lineChartDescription);
            description.setTextColor(labelColor);

            initChartView(lineData, description);
        }
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    private void initChartView(@NonNull LineData lineData, @NonNull Description description) {
        viewBinding.routeLineElevationChart.setDescription(description);
        viewBinding.routeLineElevationChart.setData(lineData);
        viewBinding.routeLineElevationChart.getLegend()
                .setTextColor(Color.WHITE);
        viewBinding.routeLineElevationChart.invalidate();
    }
}

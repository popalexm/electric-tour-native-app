package com.grandtour.ev.evgrandtour.ui.elevationView;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.databinding.FragmentRouteAltitudeStatsBinding;
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

public class ElevationInfoFragment extends BaseBottomDialogFragment implements ElevationInfoContract.View {

    @NonNull
    private static final String BUNDLE_ROUTE_LEG_ID = "routeLegId";
    @NonNull
    public static final String TAG = ElevationInfoFragment.class.getSimpleName();
    @NonNull
    private final ElevationInfoPresenter presenter = new ElevationInfoPresenter(this);
    @Nullable
    private Integer routeLegId;
    private FragmentRouteAltitudeStatsBinding viewBinding;

    public static ElevationInfoFragment newInstance(int routeLegId) {
        ElevationInfoFragment elevationInfoFragment = new ElevationInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ElevationInfoFragment.BUNDLE_ROUTE_LEG_ID, routeLegId);
        elevationInfoFragment.setArguments(args);
        return elevationInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            routeLegId = bundle.getInt(ElevationInfoFragment.BUNDLE_ROUTE_LEG_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_route_altitude_stats, null, false);
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
            presenter.onFragmentReady(routeLegId);
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

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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ElevationFragment extends BaseBottomDialogFragment implements ElevationContract.View {

    @NonNull
    private static final String BUNDLE_ROUTE_LEG_ID = "routeLegId";
    @NonNull
    public static final String TAG = ElevationFragment.class.getSimpleName();
    @NonNull
    private final ElevationPresenter presenter = new ElevationPresenter(this);
    @Nullable
    private Integer routeLegId;
    private FragmentRouteAltitudeStatsBinding viewBinding;

    public static ElevationFragment newInstance(int routeLegId) {
        ElevationFragment elevationFragment = new ElevationFragment();
        Bundle args = new Bundle();
        args.putInt(ElevationFragment.BUNDLE_ROUTE_LEG_ID, routeLegId);
        elevationFragment.setArguments(args);
        return elevationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            routeLegId = bundle.getInt(ElevationFragment.BUNDLE_ROUTE_LEG_ID);
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
            int highLightColor = Injection.provideGlobalContext()
                    .getResources()
                    .getColor(R.color.colorWhite);
            String lineLabel = Injection.provideGlobalContext()
                    .getResources()
                    .getString(R.string.label_line_chart);
            String lineChartDescription = Injection.provideGlobalContext()
                    .getResources()
                    .getString(R.string.label_line_chart_values);

            LineDataSet dataSet = new LineDataSet(elevationPointsList, lineLabel);
            dataSet.setValueTextColor(highLightColor);
            LineData lineData = new LineData(dataSet);
            Description description = new Description();
            description.setText(lineChartDescription);
            description.setTextColor(labelColor);
            viewBinding.routeLineElevationChart.setDescription(description);
            viewBinding.routeLineElevationChart.setData(lineData);
            viewBinding.routeLineElevationChart.invalidate();
        }
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }
}

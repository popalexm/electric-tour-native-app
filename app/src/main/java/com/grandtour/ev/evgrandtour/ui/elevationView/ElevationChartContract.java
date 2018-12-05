package com.grandtour.ev.evgrandtour.ui.elevationView;

import com.github.mikephil.charting.data.Entry;
import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

import java.util.List;

public class ElevationChartContract extends BaseContract {

    public interface View {

        void displayChart(@NonNull List<Entry> elevationPointsList);

        void dismissDialog();

    }

    public interface Presenter {

        void onDisplayElevationChartForRouteLeg(@NonNull Integer routeLegId);

        void onDisplayElevationChartForEntireTour();

        void onDismissButtonClicked();
    }
}

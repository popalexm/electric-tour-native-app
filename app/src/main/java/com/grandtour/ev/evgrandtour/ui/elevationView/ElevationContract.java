package com.grandtour.ev.evgrandtour.ui.elevationView;

import com.github.mikephil.charting.data.Entry;
import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

import java.util.List;

public class ElevationContract extends BaseContract {

    public interface View {

        void displayChart(@NonNull List<Entry> elevationPointsList);

        void dismissDialog();

    }

    public interface Presenter {

        void onFragmentReady(@NonNull Integer routeLegId);

        void onDismissButtonClicked();
    }
}

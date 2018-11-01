package com.grandtour.ev.evgrandtour.ui.mapsView.chooseTour;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

import java.util.List;

public class ChooseTourDialogContract extends BaseContract {

    public interface View extends BaseContract.View {

        void loadAvailableTours(@NonNull List<TourModel> tours);

        void dismissDialog();

    }

    public interface Presenter extends BaseContract.Presenter, TourClickedListener {

        void onTourChosen();

        void onDismissButtonClicked();

    }

}

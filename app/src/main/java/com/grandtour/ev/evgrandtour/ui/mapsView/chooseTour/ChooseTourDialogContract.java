package com.grandtour.ev.evgrandtour.ui.mapsView.chooseTour;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.mapsView.SelectedTourListener;

import android.support.annotation.NonNull;

import java.util.List;

public class ChooseTourDialogContract extends BaseContract {

    public interface View extends BaseContract.View {

        void loadAvailableTours(@NonNull List<TourModel> tours);

        void dismissDialog();

        void saveSelectionAndDismiss(@NonNull String tourId);
    }

    public interface Presenter extends BaseContract.Presenter, TourClickedListener {

        void OnDismissButtonClicked();

        void OnSelectionSaved(@NonNull SelectedTourListener callback, @NonNull String tourId);
    }

}

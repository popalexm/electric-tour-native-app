package com.grandtour.ev.evgrandtour.ui.chooseTour;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;
import com.grandtour.ev.evgrandtour.ui.currentTripView.listeners.OnSelectedTourListener;

import android.support.annotation.NonNull;

import java.util.List;

public class ChooseTourDialogContract extends BaseContract {

    public interface View extends BaseContract.View {

        void loadAvailableTours(@NonNull List<TourModel> tours);

        void dismissDialog();

        void saveSelectionAndDismiss(@NonNull String tourId);
    }

    public interface Presenter extends BaseContract.Presenter, TourClickedListener {

        void onDismissButtonClicked();

        void OnSelectionSaved(@NonNull OnSelectedTourListener callback, @NonNull String tourId);
    }

}

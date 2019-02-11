package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

public class NewTripCheckpointDetailsFragmentContract {

    public interface View extends BaseContract.View {

        void dismissDetailsDialog();
    }

    public interface Presenter extends BaseContract.Presenter {

        void onSaveCheckpointDetailsClicked();

        void onDismissButtonClicked();
    }

}

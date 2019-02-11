package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

public class NewTripCheckpointDetailsFragmentContract {

    public interface NewTripCheckpointDetailsFragmentView extends BaseContract.View {

    }

    public interface NewTripCheckpointDetailsFragmentPresenter extends BaseContract.Presenter {

        void onSaveCheckpointDetailsClicked();

        void onDismissButtonClicked();
    }

}

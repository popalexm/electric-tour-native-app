package com.grandtour.ev.evgrandtour.ui.addNewTrip;

import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

import java.util.List;

public class AddNewTripContract {

    public interface View extends BaseContract.View {

        void loadAllSavedTripCheckpoints(@NonNull List<Marker> checkpoints);

        void addNewCheckpointOnMap(@NonNull Marker newCheckpoint);
    }

    public interface Presenter extends BaseContract.Presenter {

    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.support.annotation.NonNull;

import java.util.List;

public class PlanNewTripContract {

    public interface View extends BaseContract.View {

        void loadAllSavedTripCheckpoints(@NonNull List<Marker> checkpoints);

        void addNewCheckpointOnMap(@NonNull Marker newCheckpoint);

        void openNewCheckpointDetailsDialog();
    }

    public interface Presenter extends BaseContract.Presenter {

        void onAddNewCheckpointClicked();

        void onMapLocationClicked(@NonNull LatLng clickedLocation);
    }
}

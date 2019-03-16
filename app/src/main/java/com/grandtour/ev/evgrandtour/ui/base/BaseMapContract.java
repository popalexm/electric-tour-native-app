package com.grandtour.ev.evgrandtour.ui.base;

import com.grandtour.ev.evgrandtour.ui.planNewTripView.models.TripCheckpoint;

import java.util.List;

import androidx.annotation.NonNull;

public class BaseMapContract extends BaseContract {

    public interface View extends BaseContract.View {

        void centerCameraOnCheckpoints(@NonNull List<TripCheckpoint> routeCheckpoints);
    }

    public interface Presenter extends BaseContract.Presenter {

    }
}

package com.grandtour.ev.evgrandtour.ui.planNewTripView.newTripCheckpointDetails;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

public class NewTripCheckpointDetailsViewModel {

    public ObservableField<String> checkpointName = new ObservableField<>("");
    public ObservableField<String> checkpointDescription = new ObservableField<>("");
    public ObservableBoolean areArrivalNotificationsEnabled = new ObservableBoolean();
    public ObservableBoolean areDepartureNotificationsEnabled = new ObservableBoolean();

}

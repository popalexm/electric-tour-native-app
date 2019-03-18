package com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

public class CreateNewTripViewModel {

    @NonNull
    public ObservableField<String> newTripName = new ObservableField<>("");
    @NonNull
    public ObservableField<String> newTripDescription = new ObservableField<>("");
    @NonNull
    public ObservableField<String> newTripNameErrorMessage = new ObservableField<>("");
    @NonNull
    public ObservableBoolean isLoadingInProgress = new ObservableBoolean(false);

}

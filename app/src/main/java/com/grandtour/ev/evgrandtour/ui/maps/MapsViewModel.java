package com.grandtour.ev.evgrandtour.ui.maps;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

public class MapsViewModel {

    public final ObservableField<String> progressMessage = new ObservableField<>("");
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    public final ObservableBoolean isCancelEnabled = new ObservableBoolean();

}

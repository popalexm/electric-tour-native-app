package com.grandtour.ev.evgrandtour.ui.maps;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class MapsViewModel {

    @NonNull
    public final ObservableField<String> progressMessage = new ObservableField<>("");
    @NonNull
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isCancelEnabled = new ObservableBoolean();

}

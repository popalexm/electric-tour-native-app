package com.grandtour.ev.evgrandtour.ui.mainMapsView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.CurrentUserLocation;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.models.MapCheckpoint;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

public class MapsViewModel {

    @NonNull
    public final ObservableBoolean isLoadingInProgress = new ObservableBoolean();
    @NonNull
    public final ObservableArrayList<Polyline> routePolyLine = new ObservableArrayList<>();
    @NonNull
    public final ObservableArrayList<MapCheckpoint> routeCheckpoints = new ObservableArrayList<>();
    @NonNull
    public final ObservableField<CurrentUserLocation> currentUserLocation = new ObservableField<>();
    @NonNull
    public final ObservableField<Marker> currentSelectedMarker = new ObservableField<>();
    @NonNull
    public final ObservableField<String> totalRouteInformation = new ObservableField<>("");
    @NonNull
    public final ObservableBoolean isRouteLengthAvailable = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isSearchViewOpen = new ObservableBoolean();
    @NonNull
    public final ObservableBoolean isWarningState = new ObservableBoolean();

    @BindingAdapter("isWarningState")
    public static void setViewStateAsWarning(View view, boolean isWarning) {
        if (isWarning) {
            AnimationManager.getInstance()
                    .shakeTextView((TextView) view);
        } else {
            view.setAnimation(null);
        }
    }
}

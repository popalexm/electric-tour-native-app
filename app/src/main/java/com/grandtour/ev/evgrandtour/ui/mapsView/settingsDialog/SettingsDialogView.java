package com.grandtour.ev.evgrandtour.ui.mapsView.settingsDialog;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsDialogView extends DialogFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    @NonNull
    public static final String TAG = SettingsDialogView.class.getSimpleName();
    @NonNull
    private final SharedPreferences preferences = Injection.provideSharedPreferences();
    @NonNull
    private Switch switchRouteDeviationNotification;
    @NonNull
    private Switch switchLocationTracking;
    @NonNull
    private Button btnDismiss;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings, null);
        switchLocationTracking = view.findViewById(R.id.switchLocation);
        switchRouteDeviationNotification = view.findViewById(R.id.switchDeviationNotifications);
        btnDismiss = view.findViewById(R.id.buttonDismiss);

        btnDismiss.setOnClickListener(this);
        switchLocationTracking.setOnCheckedChangeListener(this);
        switchRouteDeviationNotification.setOnCheckedChangeListener(this);
        setupCurrentSettings();
        return view;
    }

    private void setupCurrentSettings() {
        boolean areLocationUpdatesEnabled = preferences.getBoolean(SharedPreferencesKeys.KEY_LOCATION_TRACKING_ENABLED, true);
        boolean areDeviationNotificationsEnabled = preferences.getBoolean(SharedPreferencesKeys.KEY_ROUTE_DEVIATION_NOTIFICATIONS_ENABLED, true);
        if (areLocationUpdatesEnabled) {
            switchLocationTracking.setChecked(true);
        }
        if (areDeviationNotificationsEnabled) {
            switchRouteDeviationNotification.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        UpdateSettingsListener listener = (UpdateSettingsListener) getTargetFragment();
        switch (buttonView.getId()) {
            case R.id.switchLocation:
                if (listener != null) {
                    listener.OnLocationTrackingSettingsUpdate(isChecked);
                }
                break;
            case R.id.switchDeviationNotifications:
                if (listener != null) {
                    listener.OnRouteDeviationTrackingUpdate(isChecked);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}

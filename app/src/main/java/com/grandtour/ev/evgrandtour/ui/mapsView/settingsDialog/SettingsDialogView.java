package com.grandtour.ev.evgrandtour.ui.mapsView.settingsDialog;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.SharedPreferencesKeys;
import com.grandtour.ev.evgrandtour.ui.base.BaseDialogFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsDialogView extends BaseDialogFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

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
    private boolean areLocationUpdatesEnabled;
    private boolean areDeviationNotificationsEnabled;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_settings, null);
        switchLocationTracking = view.findViewById(R.id.switchLocation);
        switchRouteDeviationNotification = view.findViewById(R.id.switchDeviationNotifications);
        btnDismiss = view.findViewById(R.id.buttonDismiss);

        btnDismiss.setOnClickListener(this);
        switchLocationTracking.setOnCheckedChangeListener(this);
        switchRouteDeviationNotification.setOnCheckedChangeListener(this);

        setupCurrentSettings();
        setupTransparentDialogBackground();
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
        UpdateSettingsListener callback = (UpdateSettingsListener) getTargetFragment();
        switch (buttonView.getId()) {
            case R.id.switchLocation:
                if (callback != null) {
                    if (areDeviationNotificationsEnabled && !isChecked) {
                        updateLocationUpdateStatus(false, callback);
                        updateRouteDeviationNotificationStatus(false, callback);
                    }
                    if (!areDeviationNotificationsEnabled && isChecked) {
                        updateLocationUpdateStatus(true, callback);
                    }
                    if (!areDeviationNotificationsEnabled && !isChecked) {
                        updateLocationUpdateStatus(false, callback);
                    }
                }
                break;
            case R.id.switchDeviationNotifications:
                if (callback != null) {
                    if (!isChecked) {
                        updateRouteDeviationNotificationStatus(false, callback);
                    }
                    if (areLocationUpdatesEnabled && isChecked) {
                        updateRouteDeviationNotificationStatus(true, callback);
                    }
                    if (!areLocationUpdatesEnabled && isChecked) {
                        switchLocationTracking.setChecked(true);
                        updateRouteDeviationNotificationStatus(true, callback);
                        updateRouteDeviationNotificationStatus(true, callback);
                    }
                }
                break;
        }
    }

    private void updateLocationUpdateStatus(boolean areLocationTrackingEnabled, @NonNull UpdateSettingsListener listener) {
        preferences.edit()
                .putBoolean(SharedPreferencesKeys.KEY_LOCATION_TRACKING_ENABLED, areLocationTrackingEnabled)
                .commit();
        this.areLocationUpdatesEnabled = areLocationTrackingEnabled;
        listener.OnLocationTrackingSettingsUpdate(areLocationTrackingEnabled);
    }

    private void updateRouteDeviationNotificationStatus(boolean areRouteDeviationNotificationEnabled, @NonNull UpdateSettingsListener listener) {
        preferences.edit()
                .putBoolean(SharedPreferencesKeys.KEY_ROUTE_DEVIATION_NOTIFICATIONS_ENABLED, areRouteDeviationNotificationEnabled)
                .commit();
        this.areDeviationNotificationsEnabled = areRouteDeviationNotificationEnabled;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}

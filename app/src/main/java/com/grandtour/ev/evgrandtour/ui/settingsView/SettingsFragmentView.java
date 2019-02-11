package com.grandtour.ev.evgrandtour.ui.settingsView;

import com.grandtour.ev.evgrandtour.BuildConfig;
import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.data.SharedPreferencesKeys;
import com.grandtour.ev.evgrandtour.databinding.FragmentSettingsViewBinding;
import com.grandtour.ev.evgrandtour.ui.base.BaseFragment;
import com.grandtour.ev.evgrandtour.ui.signIn.SignInActivityView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

public class SettingsFragmentView extends BaseFragment<SettingsFragmentPresenter>
        implements SettingsFragmentContract.View, CompoundButton.OnCheckedChangeListener {

    @NonNull
    public static final String TAG = SettingsFragmentView.class.getSimpleName();
    @NonNull
    private final SharedPreferences preferences = Injection.provideSharedPreferences();
    @NonNull
    private final SettingsFragmentViewModel viewModel = new SettingsFragmentViewModel();
    @NonNull
    private FragmentSettingsViewBinding viewBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_view, container, false);
        viewBinding.setPresenter(getPresenter());
        viewBinding.setViewModel(viewModel);
        viewBinding.switchLocation.setOnCheckedChangeListener(this);
        viewBinding.switchDeviationNotifications.setOnCheckedChangeListener(this);
        setupCurrentSettings();
        return viewBinding.getRoot();
    }

    @NonNull
    public static SettingsFragmentView createInstance() {
        return new SettingsFragmentView();
    }

    private void setupCurrentSettings() {
        boolean areLocationUpdatesEnabled = preferences.getBoolean(SharedPreferencesKeys.KEY_LOCATION_TRACKING_ENABLED, false);
        boolean areDeviationNotificationsEnabled = preferences.getBoolean(SharedPreferencesKeys.KEY_ROUTE_DEVIATION_NOTIFICATIONS_ENABLED, false);
        if (areLocationUpdatesEnabled) {
            viewBinding.switchLocation.setChecked(true);
        }
        if (areDeviationNotificationsEnabled) {
            viewBinding.switchDeviationNotifications.setChecked(true);
        }
        String appVersion = BuildConfig.VERSION_NAME;
        String appVersionPrefix = getString(R.string.application_version);
        viewModel.appVersion.set(getString(R.string.format_app_version, appVersionPrefix, appVersion));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        UpdateSettingsListener callback = (UpdateSettingsListener) getParentFragment();
        switch (buttonView.getId()) {
            case R.id.switchLocation:
                if (callback != null) {
                    updateLocationUpdateStatus(isChecked);
                    callback.OnLocationTrackingSettingsUpdate(isChecked);
                }
                break;
            case R.id.switchDeviationNotifications:
                updateRouteDeviationNotificationStatus(isChecked);
                break;
        }
    }

    private boolean updateLocationUpdateStatus(boolean areLocationTrackingEnabled) {
        return preferences.edit()
                .putBoolean(SharedPreferencesKeys.KEY_LOCATION_TRACKING_ENABLED, areLocationTrackingEnabled)
                .commit();

    }

    private boolean updateRouteDeviationNotificationStatus(boolean areRouteDeviationNotificationEnabled) {
        return preferences.edit()
                .putBoolean(SharedPreferencesKeys.KEY_ROUTE_DEVIATION_NOTIFICATIONS_ENABLED, areRouteDeviationNotificationEnabled)
                .commit();
    }

    @Override
    public void switchToLoginScreen() {
        Activity activity = getActivity();
        if (activity != null) {
            Intent startMapsActivity = new Intent(getActivity(), SignInActivityView.class);
            startActivity(startMapsActivity);
            activity.finish();
        }
    }

    @Override
    public void showLoadingView(boolean isLoading) {
    }

    @Override
    public void showMessage(@NonNull String msg) {
    }

    @Override
    public SettingsFragmentPresenter createPresenter() {
        return new SettingsFragmentPresenter(this);
    }
}

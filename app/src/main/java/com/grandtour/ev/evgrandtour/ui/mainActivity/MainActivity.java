package com.grandtour.ev.evgrandtour.ui.mainActivity;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.ActivityMainBinding;
import com.grandtour.ev.evgrandtour.ui.currentTripView.CurrentTripFragmentView;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.PlanNewTripFragmentView;
import com.grandtour.ev.evgrandtour.ui.settingsView.SettingsFragmentView;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(this);
        loadCurrentTripView();
    }

    private void loadCurrentTripView() {
        CurrentTripFragmentView fragmentView = CurrentTripFragmentView.createInstance();
        replaceInFragmentManager(fragmentView, CurrentTripFragmentView.TAG);
    }

    private void loadAddEditTripsFragment() {
        PlanNewTripFragmentView newTripFragmentView = PlanNewTripFragmentView.createInstance();
        replaceInFragmentManager(newTripFragmentView, PlanNewTripFragmentView.TAG);
    }

    private void loadSettingsFragment() {
        SettingsFragmentView settingsFragmentView = SettingsFragmentView.createInstance();
        replaceInFragmentManager(settingsFragmentView, SettingsFragmentView.TAG);
    }

    private void replaceInFragmentManager(@NonNull Fragment fragment, @NonNull String tag) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content_frame, fragment , tag)
                    .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_current_trip:
                loadCurrentTripView();
                return true;
            case R.id.action_create_trip:
                loadAddEditTripsFragment();
                return true;
            case R.id.action_settings:
                loadSettingsFragment();
                return true;
        }
        return false;
    }
}

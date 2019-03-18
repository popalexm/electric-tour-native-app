package com.grandtour.ev.evgrandtour.ui.mainActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.ActivityMainBinding;
import com.grandtour.ev.evgrandtour.ui.currentTripView.CurrentTripFragmentView;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.PlanNewTripFragment;
import com.grandtour.ev.evgrandtour.ui.planNewTripView.createNewTrip.CreateNewTripFragment;
import com.grandtour.ev.evgrandtour.ui.settingsView.SettingsFragmentView;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationFlowListener {

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
        PlanNewTripFragment newTripFragmentView = PlanNewTripFragment.createInstance();
        replaceInFragmentManager(newTripFragmentView, PlanNewTripFragment.TAG);
    }

    private void loadCreateNewTripFragment() {
        CreateNewTripFragment createNewTripFragment = CreateNewTripFragment.createInstance();
        replaceInFragmentManager(createNewTripFragment, CreateNewTripFragment.TAG);
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
                //  loadAddEditTripsFragment();
                loadCreateNewTripFragment();
                return true;
            case R.id.action_settings:
                loadSettingsFragment();
                return true;
        }
        return false;
    }

    @Override
    public void moveToFragment(@NonNull Fragment fragment, @NonNull String tag) {
        replaceInFragmentManager(fragment, tag);
    }
}

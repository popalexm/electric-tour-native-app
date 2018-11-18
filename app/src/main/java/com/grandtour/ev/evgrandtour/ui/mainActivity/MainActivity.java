package com.grandtour.ev.evgrandtour.ui.mainActivity;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.ActivityMainBinding;
import com.grandtour.ev.evgrandtour.ui.animations.AnimationManager;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.MapsFragmentView;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , Toolbar.OnMenuItemClickListener{

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupViews();
        setupGeoFenceMapFragment();
    }

    private void setupViews() {
        if (binding != null) {
            binding.fabOpenTourSelection.setOnClickListener(this);
            binding.bottomAppBar.replaceMenu(R.menu.menu_main);
            binding.bottomAppBar.setNavigationOnClickListener(this);
            binding.bottomAppBar.setOnMenuItemClickListener(this);
        }
    }

    public void animateRouteSelectionButton() {
        if (binding != null) {
            AnimationManager.getInstance()
                    .startBounceAnimation(binding.fabOpenTourSelection);
        }
    }

    private void setupGeoFenceMapFragment() {
            MapsFragmentView fragmentView = MapsFragmentView.createInstance();
            replaceInFragmentManager(fragmentView , MapsFragmentView.TAG);
    }

    private void replaceInFragmentManager(@NonNull Fragment fragment, @NonNull String tag) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content_frame, fragment , tag)
                    .commit();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        MapsFragmentView mapsFragmentView = (MapsFragmentView) getSupportFragmentManager().findFragmentByTag(MapsFragmentView.TAG);
        switch (item.getItemId()) {
            case R.id.action_distance_between_2_checkpoints:
                if (mapsFragmentView != null) {
                    mapsFragmentView.onCalculateDistanceBetweenCheckpoints();
                }
                break;
            case R.id.action_settings:
                if (mapsFragmentView != null) {
                    mapsFragmentView.openSettingsDialog();
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_open_tour_selection) {
            MapsFragmentView mapsFragmentView = (MapsFragmentView) getSupportFragmentManager().findFragmentByTag(MapsFragmentView.TAG);
            if (mapsFragmentView != null) {
                mapsFragmentView.onChooseTourClicked();
            }
        }
    }
}

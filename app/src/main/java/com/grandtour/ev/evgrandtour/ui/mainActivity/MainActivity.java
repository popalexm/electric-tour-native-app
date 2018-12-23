package com.grandtour.ev.evgrandtour.ui.mainActivity;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.MapsFragmentView;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupGeoFenceMapFragment();
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
            case R.id.action_view_route_altitude:
                if (mapsFragmentView != null) {
                    mapsFragmentView.openEntireTourElevationChart();
                }
                break;
        }
        return true;
    }
}

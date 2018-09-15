package com.grandtour.ev.evgrandtour.main;


import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.maps.MapsFragmentView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , Toolbar.OnMenuItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomAppBar bottomAppBar= findViewById(R.id.bottomAppBar);

        FloatingActionButton calculateRoutesFab = findViewById(R.id.fab_calculate_routes);
        calculateRoutesFab.setOnClickListener(this);

        bottomAppBar.replaceMenu(R.menu.menu_main);
        bottomAppBar.setNavigationOnClickListener(this);
        bottomAppBar.setOnMenuItemClickListener(this);
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
            case R.id.action_add_waypoints :
                if (mapsFragmentView != null) {
                    mapsFragmentView.openFileExplorer();
                }
                break;
            case R.id.action_delete_waypoints :
                if (mapsFragmentView != null) {
                    mapsFragmentView.clearWaypointsClicked();
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        MapsFragmentView mapsFragmentView = (MapsFragmentView) getSupportFragmentManager().findFragmentByTag(MapsFragmentView.TAG);
        if (mapsFragmentView != null) {
            mapsFragmentView.calculateRoutesClicked();
        }
    }
}

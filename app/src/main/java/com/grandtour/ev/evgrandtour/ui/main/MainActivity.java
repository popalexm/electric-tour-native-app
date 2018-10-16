package com.grandtour.ev.evgrandtour.ui.main;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.maps.MapsFragmentView;
import com.grandtour.ev.evgrandtour.ui.utils.DialogUtils;

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
                    showChoicesDialog(mapsFragmentView);
                   // mapsFragmentView.openFileExplorer();
                }
                break;
            case R.id.action_delete_waypoints :
                if (mapsFragmentView != null) {
                    mapsFragmentView.clearMapDataClicked();
                }
                break;
            case R.id.action_calculate_routes:
                if (mapsFragmentView != null) {
                    mapsFragmentView.calculateRoutesClicked();
                }
                break;

            case R.id.action_route_lenght_info:
                if (mapsFragmentView != null) {
                    mapsFragmentView.onTotalRouteLengthClicked();
                }
                break;
            case R.id.action_distance_between_2_checkpoints:
                if (mapsFragmentView != null) {
                    mapsFragmentView.onCalculateDistanceBetweenCheckpoints();
                }
                break;
        }
        return true;
    }

    private void showChoicesDialog(@NonNull MapsFragmentView mapsFragmentView) {
        String CHOICE_UPLOAD = "Upload checkpoints from a local file";
        String CHOICE_SYNC_NEXT_DAY = "Sync checkpoints for the next day";
        String CHOICE_SYNC_ALL = "Sync all available checkpoints";

        String[] choices = {CHOICE_UPLOAD, CHOICE_SYNC_ALL ,CHOICE_SYNC_NEXT_DAY};
        DialogUtils.getMultipleChoicesAlertDialogBuilder(this, choices, "Choose an option", new DialogUtils.DialogChoiceCallback() {
            @Override
            public void onDialogChoiceSelected(String choice) {
               if (choice.equals(CHOICE_UPLOAD)) {
                   mapsFragmentView.openFileExplorer();
               }
               if (choice.equals(CHOICE_SYNC_NEXT_DAY)) {

               }
               if (choice.equals(CHOICE_SYNC_ALL)){
                   mapsFragmentView.onSyncEntireTourClicked();
               }
            }
        }).show();
    }

    @Override
    public void onClick(View v) {
        MapsFragmentView mapsFragmentView = (MapsFragmentView) getSupportFragmentManager().findFragmentByTag(MapsFragmentView.TAG);
        if (mapsFragmentView != null) {
            mapsFragmentView.openNavigationForSelectedMarker();
        }
    }
}

package com.grandtour.ev.evgrandtour.ui.mainActivity;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.ui.mainMapsView.MapsFragmentView;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupViewTripsMapFragment();
    }

    private void setupViewTripsMapFragment() {
            MapsFragmentView fragmentView = MapsFragmentView.createInstance();
            replaceInFragmentManager(fragmentView , MapsFragmentView.TAG);
    }

    private void replaceInFragmentManager(@NonNull Fragment fragment, @NonNull String tag) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content_frame, fragment , tag)
                    .commit();
    }
}

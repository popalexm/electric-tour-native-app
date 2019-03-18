package com.grandtour.ev.evgrandtour.ui.mainActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public interface NavigationFlowListener {

    void moveToFragment(@NonNull Fragment fragment, @NonNull String tag);

}

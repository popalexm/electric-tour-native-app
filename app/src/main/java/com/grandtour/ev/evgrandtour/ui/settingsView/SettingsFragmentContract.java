package com.grandtour.ev.evgrandtour.ui.settingsView;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

public class SettingsFragmentContract {

    public interface View extends BaseContract.View {

        void switchToLoginScreen();
    }

    public interface Presenter {

        void onSignOutButtonClicked();

    }
}

package com.grandtour.ev.evgrandtour.ui.settings;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

public class SettingsFragmentPresenter extends BasePresenter implements SettingsFragmentContract.Presenter {

    @NonNull
    private static final String USER_TOKEN = "user_token";

    @NonNull
    private final SettingsFragmentContract.View view;

    SettingsFragmentPresenter(@NonNull SettingsFragmentContract.View view) {
        this.view = view;
    }

    @Override
    public void onSignOutButtonClicked() {
        boolean isTokenRemoved = Injection.provideSharedPreferences()
                .edit()
                .remove(SettingsFragmentPresenter.USER_TOKEN)
                .commit();
        if (isTokenRemoved && isViewAttached) {
            view.switchToLoginScreen();
        }
    }

}

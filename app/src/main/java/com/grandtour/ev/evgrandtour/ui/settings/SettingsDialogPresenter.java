package com.grandtour.ev.evgrandtour.ui.settings;

import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.support.annotation.NonNull;

public class SettingsDialogPresenter extends BasePresenter implements SettingsDialogContract.Presenter {

    @NonNull
    private static final String USER_TOKEN = "user_token";

    @NonNull
    private final SettingsDialogContract.View view;

    SettingsDialogPresenter(@NonNull SettingsDialogContract.View view) {
        this.view = view;
    }

    @Override
    public void onSignOutButtonClicked() {
        boolean isTokenRemoved = Injection.provideSharedPreferences()
                .edit()
                .remove(SettingsDialogPresenter.USER_TOKEN)
                .commit();
        if (isTokenRemoved && isViewAttached) {
            view.switchToLoginScreen();
        }
    }
}

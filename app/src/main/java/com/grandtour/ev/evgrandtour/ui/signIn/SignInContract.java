package com.grandtour.ev.evgrandtour.ui.signIn;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;

import com.grandtour.ev.evgrandtour.ui.base.BaseContract;

import android.content.Intent;
import android.support.annotation.NonNull;

public class SignInContract {

    public interface View extends BaseContract.View {

        void openGoogleSignInDialog(@NonNull Intent googleSignInIntent);

        void moveToMainMapScreen();
    }

    public interface Presenter {

        void onGoogleSignInButtonClicked();

        void onGoogleSingInComplete(@NonNull GoogleSignInAccount googleSignInAccount);

        void onGoogleSignInFailed(@NonNull ApiException exception);
    }
}

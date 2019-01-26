package com.grandtour.ev.evgrandtour.ui.signIn;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.app.Injection;
import com.grandtour.ev.evgrandtour.ui.base.BasePresenter;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SignInPresenter extends BasePresenter implements SignInContract.Presenter, OnCompleteListener<AuthResult> {

    @NonNull
    private static final String TAG = SignInPresenter.class.getSimpleName();
    static final int RC_SIGN_IN = 9001;
    @NonNull
    private static final String PREFERENCES_TOKEN = "user_token";
    @NonNull
    private final SignInContract.View view;
    @NonNull
    private final FirebaseAuth mFireBaseAuth;
    @NonNull
    private final GoogleSignInClient mGoogleSignInClient;

    SignInPresenter(@NonNull SignInContract.View view) {
        this.view = view;
        mGoogleSignInClient = GoogleSignIn.getClient(Injection.provideGlobalContext(), buildSignInOptions());
        mFireBaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    private GoogleSignInOptions buildSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Injection.provideGlobalContext()
                .getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    @Override
    public void onAttachView() {
        super.onAttachView();
        checkPreviousLoginStatus();
    }

    @Override
    public void onGoogleSignInButtonClicked() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        view.openGoogleSignInDialog(signInIntent);
    }

    @Override
    public void onGoogleSingInComplete(@NonNull GoogleSignInAccount googleSignInAccount) {
        if (isViewAttached) {
            view.showLoadingView(true);
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this);
    }

    @Override
    public void onGoogleSignInFailed(@NonNull ApiException exception) {
        String error = exception.getMessage();
        if (error != null && isViewAttached) {
            view.showMessage(Injection.provideGlobalContext()
                    .getString(R.string.message_sign_in_with_google_failed));
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser firebaseUser = mFireBaseAuth.getCurrentUser();
            if (isViewAttached) {
                view.showMessage(Injection.provideGlobalContext()
                        .getString(R.string.message_sign_in_successful));
            }
            if (firebaseUser != null) {
                validateUserTokenOnBackend(firebaseUser);
            }
        } else {
            // If sign in fails, display a message to the user.
            Exception exception = task.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
            if (isViewAttached) {
                view.showMessage(Injection.provideGlobalContext()
                        .getString(R.string.message_sign_in_failed));
            }
        }
    }

    private void checkPreviousLoginStatus() {
        String token = Injection.provideSharedPreferences()
                .getString(SignInPresenter.PREFERENCES_TOKEN, "");
        if (token != null && token.length() > 0) {
            view.moveToMainMapScreen();
        }
    }

    private void validateUserTokenOnBackend(@NonNull FirebaseUser user) {
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {
                        String token = task.getResult()
                                .getToken();
                        addSubscription(Injection.provideBackendApi()
                                .validateUserToken(token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnError(throwable -> {
                                    if (isViewAttached) {
                                        view.showLoadingView(false);
                                        view.showMessage(Injection.provideGlobalContext()
                                                .getString(R.string.message_account_not_authorized));
                                    }
                                })
                                .doOnComplete(() -> {
                                    if (isViewAttached) {
                                        Injection.provideSharedPreferences()
                                                .edit()
                                                .putString(SignInPresenter.PREFERENCES_TOKEN, token)
                                                .apply();
                                        view.showLoadingView(false);
                                        view.moveToMainMapScreen();
                                    }

                                })
                                .subscribe());
                    }
                });
    }
}

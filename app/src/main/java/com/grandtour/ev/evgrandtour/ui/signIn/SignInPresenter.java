package com.grandtour.ev.evgrandtour.ui.signIn;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SignInPresenter extends BasePresenter implements SignInContract.Presenter, OnCompleteListener<AuthResult> {

    @NonNull
    private static final String TAG = SignInPresenter.class.getSimpleName();
    static final int RC_SIGN_IN = 9001;
    @NonNull
    private final SignInContract.View view;
    @NonNull
    private final FirebaseAuth mFireBaseAuth;
    @NonNull
    private final GoogleSignInClient mGoogleSignInClient;

    SignInPresenter(@NonNull SignInContract.View view, @NonNull GoogleSignInOptions googleSignInOptions) {
        this.view = view;
        mGoogleSignInClient = GoogleSignIn.getClient(Injection.provideGlobalContext(), googleSignInOptions);
        mFireBaseAuth = FirebaseAuth.getInstance();
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
                                .subscribe(new Consumer<Response>() {
                                    @Override
                                    public void accept(Response response) {
                                        if (response.isSuccessful()) {
                                            Log.e(TAG, "Response is ok");
                                        } else {
                                            Log.e(TAG, "Response is not ok , " + response.code());
                                            if (isViewAttached) {
                                                view.showLoadingView(false);
                                            }
                                        }
                                    }
                                }));
                    }
                });
    }
}

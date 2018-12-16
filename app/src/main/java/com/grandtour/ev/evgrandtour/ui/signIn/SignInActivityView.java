package com.grandtour.ev.evgrandtour.ui.signIn;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.grandtour.ev.evgrandtour.R;
import com.grandtour.ev.evgrandtour.databinding.ActivitySignInBinding;
import com.grandtour.ev.evgrandtour.ui.mainActivity.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class SignInActivityView extends Activity implements SignInContract.View {

    @NonNull
    private final String TAG = SignInActivityView.class.getSimpleName();
    @NonNull
    private SignInPresenter presenter;
    @NonNull
    private SignInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignInBinding activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        GoogleSignInOptions googleSignInOptions = buildSignInOptions();
        presenter = new SignInPresenter(this, googleSignInOptions);
        viewModel = new SignInViewModel();
        activitySignInBinding.setPresenter(presenter);
        activitySignInBinding.setViewModel(viewModel);
    }

    private GoogleSignInOptions buildSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onAttach();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignInPresenter.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    presenter.onGoogleSingInComplete(account);
                }
            } catch (ApiException exception) {
                exception.printStackTrace();
                presenter.onGoogleSignInFailed(exception);
            }
        }
    }

    @Override
    public void showLoadingView(boolean isLoading) {
        viewModel.isLoadingInProgress.set(isLoading);
    }

    @Override
    public void showMessage(@NonNull String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void openGoogleSignInDialog(@NonNull Intent googleSignInIntent) {
        startActivityForResult(googleSignInIntent, SignInPresenter.RC_SIGN_IN);
    }

    @Override
    public void moveToMainMapScreen() {
        Intent startMapsActivity = new Intent(this, MainActivity.class);
        startActivity(startMapsActivity);
        finish();
    }
}

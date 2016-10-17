package pl.elfdump.wloczykij.network.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.activity.LoginActivity;
import pl.elfdump.wloczykij.network.tasks.AuthorizationTask;
import pl.elfdump.wloczykij.utils.APICallback;

public class GoogleLoginProvider implements LoginProvider, GoogleApiClient.OnConnectionFailedListener {

    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private LoginActivity loginActivity;
    private APICallback callback;

    public static final int RC_SIGN_IN = 9001;

    public GoogleLoginProvider(LoginActivity loginActivity, APICallback callback){
        this.loginActivity = loginActivity;
        this.callback = callback;
    }

    @Override
    public void setUp() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(loginActivity.getString(R.string.server_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(loginActivity)
                .enableAutoManage(loginActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void logIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        loginActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void logOut(final APICallback apiCallback) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    apiCallback.success();
                }
            });
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            new AuthorizationTask(LoginServiceProvider.GOOGLE, acct.getIdToken(), loginActivity, callback).execute();
        } else {
            callback.failed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

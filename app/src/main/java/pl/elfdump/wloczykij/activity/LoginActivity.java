package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.LoginServiceProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult> {
    public static final int RC_GOOGLE_SIGN_IN = 9001;

    private GoogleApiClient googleApi;
    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FacebookSdk needs to be initialized before LoginButton is created
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();

        setContentView(R.layout.activity_login);

        // Configure Google API
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.google_server_client_id))
            .build();

        googleApi = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        LoginButton fbLogin = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        fbLogin.setReadPermissions("email");
        fbLogin.registerCallback(fbCallbackManager, this);

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.submit_nickname).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentPhase == null) {
            String previousToken = Wloczykij.settings.getToken();
            if (previousToken == null) {
                enterPhase(Phase.WELCOME);
            } else {
                enterPhase(Phase.IN_PROGRESS);
                Wloczykij.api.setToken(previousToken);
                afterLogin();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                enterPhase(Phase.IN_PROGRESS);
                startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApi), RC_GOOGLE_SIGN_IN);
                break;
            case R.id.submit_nickname:
                submitNickname();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                doLogin(LoginServiceProvider.GOOGLE, result.getSignInAccount().getIdToken());
            } else {
                loginFailure();
            }
        }

        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO: ?
        Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        enterPhase(Phase.IN_PROGRESS);
        doLogin(LoginServiceProvider.FACEBOOK, loginResult.getAccessToken().getToken());
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onCancel() {
        loginFailure();
    }

    @Override
    public void onError(FacebookException error) {
        loginFailure(error.toString());
    }

    /**
     * Called to asynchronously authenticate to API using given provider
     */
    private void doLogin(final LoginServiceProvider provider, final String authToken) {
        Log.d(Wloczykij.TAG, "Do login with "+provider.name()+" "+authToken);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    return Wloczykij.api.requestToken(provider, authToken);
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null) {
                    // Save the token for auto-login in the future
                    Wloczykij.settings.setToken(token);
                    afterLogin();
                } else {
                    loginFailure();
                }
            }
        }.execute();
    }

    /**
     * Called successfully authenticating to the API
     */
    private void afterLogin() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Wloczykij.session.updateUserData();
                    return true;
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    if (Wloczykij.session.loggedOnUser.isFirstLogin()) {
                        enterPhase(Phase.SET_USERNAME_ANIMATED);
                    } else {
                        enterPhase(Phase.ENTER_MAP);
                    }
                } else {
                    loginFailure();
                }
            }
        }.execute();
    }

    /**
     * Called in case of any login failure, causes return to main login screen
     */
    private void loginFailure() {
        loginFailure(getString(R.string.error_occurred));
    }

    /**
     * Called in case of any login failure, causes return to main login screen
     * @param text Error message to display
     */
    private void loginFailure(String text) {
        Log.e(Wloczykij.TAG, "Login failure");
        Thread.dumpStack();
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        enterPhase(Phase.LOGIN_SCREEN);
    }

    private void submitNickname() {
        final String nickname = ((MaterialEditText) findViewById(R.id.login_username)).getText().toString();

        new AsyncTask<Void, Void, Integer>() {
            private static final int RESULT_SUCCESS = 0;
            private static final int RESULT_ERROR = 1;
            private static final int RESULT_ALREADY_TAKEN = 2;

            @Override
            protected void onPreExecute() {
                enterPhase(Phase.IN_PROGRESS);
            }

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    if (Wloczykij.session.setUsername(nickname)) {
                        return RESULT_SUCCESS;
                    } else {
                        return RESULT_ALREADY_TAKEN;
                    }
                } catch (APIRequestException e) {
                    e.printStackTrace();
                    return RESULT_ERROR;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                switch(result) {
                    case RESULT_SUCCESS:
                        enterPhase(Phase.ENTER_MAP);
                        break;
                    case RESULT_ERROR:
                        enterPhase(Phase.SET_USERNAME);
                        Toast.makeText(getApplicationContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                        break;
                    case RESULT_ALREADY_TAKEN:
                        enterPhase(Phase.SET_USERNAME);
                        Toast.makeText(getApplicationContext(), R.string.username_busy, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }.execute();
    }

    private enum Phase {
        WELCOME,
        LOGIN_SCREEN,
        IN_PROGRESS,
        SET_USERNAME_ANIMATED,
        SET_USERNAME,
        ENTER_MAP
    }

    private Phase currentPhase = null;

    private void enterPhase(Phase phase) {
        Animation move_from_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_top);
        Animation move_from_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_bottom);
        Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        switch(phase) {
            case WELCOME:
                findViewById(R.id.app_icon).startAnimation(move_from_top);
                findViewById(R.id.login_main_layout).startAnimation(fade_in);
                findViewById(R.id.login_buttons_layout).startAnimation(move_from_bottom);
                // no break; here!

            case LOGIN_SCREEN:
                findViewById(R.id.login_with).setVisibility(View.VISIBLE);
                findViewById(R.id.login_buttons_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.facebook_sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.setup_username_layout).setVisibility(View.GONE);
                break;

            case IN_PROGRESS:
                findViewById(R.id.login_with).setVisibility(View.GONE);
                findViewById(R.id.login_buttons_layout).setVisibility(View.GONE);
                findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.facebook_sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.setup_username_layout).setVisibility(View.GONE);
                break;

            case SET_USERNAME_ANIMATED:
                findViewById(R.id.setup_username_layout).startAnimation(move_from_bottom);
            case SET_USERNAME:
                findViewById(R.id.login_with).setVisibility(View.GONE);
                findViewById(R.id.login_buttons_layout).setVisibility(View.GONE);
                findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.facebook_sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.setup_username_layout).setVisibility(View.VISIBLE);
                break;

            case ENTER_MAP:
                startActivity(new Intent(this, MapViewActivity.class));
                break;
        }
        currentPhase = phase;
    }
}

package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;

import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIBadRequestException;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.User;
import pl.elfdump.wloczykij.network.login.GoogleLoginProvider;
import pl.elfdump.wloczykij.network.login.LoginCallback;
import pl.elfdump.wloczykij.network.login.LoginProvider;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mStatusTextView;
    private LoginProvider loginProvider;

    private static final int REQUEST_ERROR = 0;
    private static final int REQUEST_OK = 1;
    private static final int REQUEST_ERROR_USERNAME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        findViewById(R.id.submit_nickname).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        String token = Wloczykij.settings.getToken();
        if (token != null){
            Wloczykij.api.setToken(token);
            nextActivity();
            return;
        }

        Animation move_from_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_top);
        Animation move_from_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_bottom);
        Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        findViewById(R.id.app_icon).startAnimation(move_from_top);
        findViewById(R.id.login_main_layout).startAnimation(fade_in);
        findViewById(R.id.login_buttons_layout).startAnimation(move_from_bottom);
    }

    private void afterLogin(final String token){

        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                User me = null;
                try {
                    me = Wloczykij.api.sendJsonRequest("GET", Wloczykij.api.getEndpointUrl("me"), null, User.class);
                } catch (APIRequestException e) {
                    e.printStackTrace();
                }

                return me;
            }

            @Override
            protected void onPostExecute(User user) {
                if(user != null){
                    Wloczykij.session.loggedOnUser = user;
                    Log.d(Wloczykij.TAG, "Followed tags: " + Arrays.toString(user.getFollowedTags()));
                    if(user.isFirstLogin()){
                        findViewById(R.id.login_with).setVisibility(View.GONE);
                        findViewById(R.id.setup_username_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.app_description).setVisibility(View.GONE);

                        Animation move_from_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_from_bottom);
                        findViewById(R.id.setup_username_layout).startAnimation(move_from_bottom);
                    }else{
                        Wloczykij.settings.setToken(token);
                        nextActivity();
                    }
                }
            }
        }.execute();

    }

    private void nextActivity(){
        Intent intent = new Intent(this, MapViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginProvider.handleResult(requestCode, resultCode, data);
    }

    private void setProvider(LoginServiceProvider loginProvider){
        if(this.loginProvider == null) {
            GoogleLoginProvider googleProvider = (GoogleLoginProvider) getLoginProvider(loginProvider);
            googleProvider.setUp();
            this.loginProvider = googleProvider;
        }
    }

    private LoginProvider getLoginProvider(LoginServiceProvider loginProvider){
        switch (loginProvider){
            case GOOGLE:
                LoginCallback googleCallback = new LoginCallback(){

                    @Override
                    public void success(final String token){
                        runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                                  findViewById(R.id.google_sign_out_layout).setVisibility(View.VISIBLE);

                                  afterLogin(token);
                              }
                        });

                    }

                    @Override
                    public void failed(){
                        runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  mStatusTextView.setText("");

                                  findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                                  findViewById(R.id.google_sign_out_layout).setVisibility(View.GONE);
                              }
                        });
                    }

                };
                return new GoogleLoginProvider(this, googleCallback);

            default:
                return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                setProvider(LoginServiceProvider.GOOGLE);
                this.loginProvider.logIn();
                break;
            case R.id.google_sign_out_button:
                setProvider(LoginServiceProvider.GOOGLE);
                this.loginProvider.logOut(new LoginCallback() {
                    @Override
                    public void success(String token) {
                        mStatusTextView.setText("");
                        findViewById(R.id.login_with).setVisibility(View.GONE);
                        findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_sign_out_layout).setVisibility(View.GONE);
                    }

                    @Override
                    public void failed() {

                    }
                });
                break;
            case R.id.submit_nickname:

                String nickname = ((MaterialEditText) findViewById(R.id.login_username)).getText().toString();

                new AsyncTask<String, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(String... params) {
                        User user;
                        try {
                            user = (User) Wloczykij.session.loggedOnUser.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                            return REQUEST_ERROR;
                        }

                        user.setUsername(params[0]);

                        try {
                            Wloczykij.api.cache(User.class).save(user);
                        } catch (APIBadRequestException e) {
                            if(e.getErrors().containsKey("username")){
                                return REQUEST_ERROR_USERNAME;
                            }
                        } catch (APIRequestException e) {
                            e.printStackTrace();
                            return REQUEST_ERROR;
                        }

                        return REQUEST_OK;
                    }

                    @Override
                    protected void onPostExecute(Integer status) {

                        if(status == REQUEST_ERROR_USERNAME){
                            Toast.makeText(getApplicationContext(), R.string.username_busy, Toast.LENGTH_SHORT).show();
                        }else if(status == REQUEST_ERROR){
                            Toast.makeText(getApplicationContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                        }else{
                            nextActivity();
                        }

                    }
                }.execute(nickname);

                break;
        }
    }
}

package pl.elfdump.wloczykij.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.login.GoogleLoginProvider;
import pl.elfdump.wloczykij.network.login.LoginProvider;
import pl.elfdump.wloczykij.R;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;
import pl.elfdump.wloczykij.utils.APICallback;
import pl.elfdump.wloczykij.utils.UserSettings;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mStatusTextView;
    private LoginProvider loginProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Wloczykij.onStart(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        UserSettings settings = Wloczykij.getSettings();
        if(settings.tokenExist()){
            Wloczykij.getSession().authToken = settings.getToken();
            nextActivity();
        }

    }

    private void afterLogin(){
        UserSettings settings = Wloczykij.getSettings();
        settings.setToken(Wloczykij.getSession().authToken);

        nextActivity();
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
                APICallback googleCallback = new APICallback(){

                    @Override
                    public void success(){
                        runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                                  findViewById(R.id.google_sign_out_layout).setVisibility(View.VISIBLE);

                                  afterLogin();
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
                this.loginProvider.logOut(new APICallback(){
                    @Override
                    public void success() {
                        mStatusTextView.setText("");

                        findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_sign_out_layout).setVisibility(View.GONE);
                    }
                });
                break;
        }
    }
}

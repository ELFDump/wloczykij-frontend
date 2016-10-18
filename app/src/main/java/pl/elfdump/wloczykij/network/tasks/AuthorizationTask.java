package pl.elfdump.wloczykij.network.tasks;

import android.os.AsyncTask;

import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;

public class AuthorizationTask extends AsyncTask<Void, Void, String> {

    private LoginServiceProvider provider;
    private String providerToken;

    public AuthorizationTask(LoginServiceProvider provider, String token){
        this.provider = provider;
        this.providerToken = token;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            return Wloczykij.api.requestToken(this.provider, this.providerToken);
        } catch (APIRequestException e) {
            e.printStackTrace();
            return null;
        }
    }
}

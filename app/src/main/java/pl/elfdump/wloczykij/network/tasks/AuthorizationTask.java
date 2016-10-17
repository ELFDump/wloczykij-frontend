package pl.elfdump.wloczykij.network.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.activity.MapViewActivity;
import pl.elfdump.wloczykij.models.Token;
import pl.elfdump.wloczykij.network.APIManager;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;
import pl.elfdump.wloczykij.network.api.AuthorizationService;
import pl.elfdump.wloczykij.utils.APICallback;

public class AuthorizationTask extends AsyncTask<Object, Void, Object> {

    private LoginServiceProvider provider;
    private String providerToken;
    private Context context;
    private APICallback callback;

    public AuthorizationTask(LoginServiceProvider provider, String token, Context context, APICallback callback){
        this.provider = provider;
        this.providerToken = token;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        AuthorizationService authService = new AuthorizationService();
        Token token = authService.authorize(provider, providerToken);

        if(token != null){
            Wloczykij.getSession().authToken = token;
            callback.success();
            Log.d(APIManager.TAG, token.token);
        }else{
            callback.failed();
        }

        return token;
    }

}

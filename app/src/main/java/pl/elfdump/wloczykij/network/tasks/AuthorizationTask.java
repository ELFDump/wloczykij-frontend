package pl.elfdump.wloczykij.network.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import pl.elfdump.wloczykij.activity.MapsActivity;
import pl.elfdump.wloczykij.models.Token;
import pl.elfdump.wloczykij.network.APIManager;
import pl.elfdump.wloczykij.network.LoginServiceProvider;
import pl.elfdump.wloczykij.network.api.AuthorizationService;
import pl.elfdump.wloczykij.utils.APICallback;

public class AuthorizationTask extends NetworkTask {

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
            callback.success();
            APIManager.getSession().authToken = token;
            Log.d(APIManager.TAG, token.token);

            Intent intent = new Intent(context, MapsActivity.class);
            context.startActivity(intent);
        }else{
            callback.failed();
        }

        return token;
    }

}

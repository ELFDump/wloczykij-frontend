package pl.elfdump.wloczykij.network.tasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import pl.elfdump.wloczykij.Session;
import pl.elfdump.wloczykij.activity.MapsActivity;
import pl.elfdump.wloczykij.models.Token;
import pl.elfdump.wloczykij.network.APICall;
import pl.elfdump.wloczykij.network.LoginServiceProvider;
import pl.elfdump.wloczykij.network.api.AuthorizationService;

public class AuthorizationTask extends NetworkTask {

    private LoginServiceProvider provider;
    private String providerToken;
    private Context context;

    public AuthorizationTask(LoginServiceProvider provider, String token, Context context){
        this.provider = provider;
        this.providerToken = token;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        AuthorizationService authService = new AuthorizationService();
        Token token = authService.authorize(provider, providerToken);

        if(token != null){
            Session.authToken = token;
            Log.d(APICall.TAG, token.token);

            Intent intent = new Intent(context, MapsActivity.class);
            context.startActivity(intent);
        }

        return token;
    }

}

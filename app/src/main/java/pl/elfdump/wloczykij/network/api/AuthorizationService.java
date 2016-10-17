package pl.elfdump.wloczykij.network.api;

import android.util.Log;
import pl.elfdump.wloczykij.exceptions.RequestException;
import pl.elfdump.wloczykij.models.Token;
import pl.elfdump.wloczykij.network.APIManager;
import pl.elfdump.wloczykij.network.APIResponse;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;
import pl.elfdump.wloczykij.utils.JsonUtils;

import java.io.IOException;

public class AuthorizationService {

    public Token authorize(LoginServiceProvider loginServiceProvider, String providerToken){
        Token apiToken = null;

        try {
            switch (loginServiceProvider) {
                case GOOGLE:
                    apiToken = authorizeGoogle(providerToken);
                    break;
                default:
                    break;
            }
        }catch (IOException e){
            Log.e(APIManager.TAG, "Failed to deserialize Token", e);
        }

        return apiToken;
    }

    private Token authorizeGoogle(String token) throws IOException{
        APIResponse response;
        try {
            response = APIManager.newCall().get("/token/google/?token=%s", token);
        } catch (RequestException e) {
            Log.e(APIManager.TAG, e.getMessage());
            return null;
        }

        return JsonUtils.deserialize(response.json(), Token.class);
    }

}

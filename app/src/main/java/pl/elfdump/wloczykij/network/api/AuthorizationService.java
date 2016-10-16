package pl.elfdump.wloczykij.network.api;

import android.util.Log;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import pl.elfdump.wloczykij.models.Token;
import pl.elfdump.wloczykij.network.APICall;
import pl.elfdump.wloczykij.network.APIResponse;
import pl.elfdump.wloczykij.network.LoginServiceProvider;

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
            Log.e(APICall.TAG, "Failed to deserialize Token", e);
        }

        return apiToken;
    }

    private Token authorizeGoogle(String token) throws IOException{
        APIResponse response = APICall.request("/token/google/?token=" + token);

        if(response.code() != 200){
            return null;
        }

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Token> jsonAdapter = moshi.adapter(Token.class);

        return jsonAdapter.fromJson(response.json());
    }

}

package pl.elfdump.wloczykij.network;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.elfdump.wloczykij.Session;
import pl.elfdump.wloczykij.models.Token;

import java.io.IOException;

public class APICall {

    public static final String TAG = "Wloczykij";
    private static final String defaultServer = "http://dom.krzysh.pl:8000";

    public static APIResponse request(String URL, Object... params){
        return requestWithToken(String.format(URL, params), Session.authToken);
    }

    public static APIResponse request(String URL, Token token, Object... params){
        return requestWithToken(String.format(URL, params), token);
    }

    public static APIResponse requestWithToken(String URL, Token token){
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder()
                .url(defaultServer + URL);

        if(token != null){
            builder.addHeader("Authorization", "Token " + token.token);
        }

        Request request = builder.build();

        Response response;
        ResponseBody body;
        try {
            response = client.newCall(request).execute();
            body = response.body();
        }catch (IOException e){
            Log.e(TAG, "Failed to send API request", e);
            return null;
        }

        int statusCode = response.code();
        String json;

        try {
            json = body.string();
        }catch (IOException e){
            Log.e(TAG, "Failed to decode API response", e);
            return null;
        }

        return new APIResponse(statusCode, json);
    }

}

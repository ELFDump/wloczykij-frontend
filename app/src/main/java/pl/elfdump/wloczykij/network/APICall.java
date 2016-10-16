package pl.elfdump.wloczykij.network;

import android.util.Log;
import com.squareup.moshi.Json;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.elfdump.wloczykij.Session;
import pl.elfdump.wloczykij.exceptions.RequestException;
import pl.elfdump.wloczykij.models.APIError;
import pl.elfdump.wloczykij.models.Token;
import pl.elfdump.wloczykij.utils.JsonUtils;

import java.io.IOException;

public class APICall {

    private Session session;

    public APICall(Session session){
        this.session = session;
    }

    public APIResponse request(String URL, Object... params) throws RequestException{
        return requestWithToken(String.format(URL, params), APIManager.getSession().authToken);
    }

    public APIResponse request(String URL, Token token, Object... params) throws RequestException{
        return requestWithToken(String.format(URL, params), token);
    }

    public APIResponse requestWithToken(String URL, Token token) throws RequestException{
        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder()
                .url(APIManager.defaultServer + URL);

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
            Log.e(APIManager.TAG, "Failed to send API request", e);
            return null;
        }

        int statusCode = response.code();
        String json;

        try {
            json = body.string();
        }catch (IOException e){
            Log.e(APIManager.TAG, "Failed to decode API response", e);
            return null;
        }

        if(response.code() != 200){
            String error = JsonUtils.deserialize(json, APIError.class).detail;
            throw new RequestException(statusCode, error);
        }

        return new APIResponse(statusCode, json);
    }

}

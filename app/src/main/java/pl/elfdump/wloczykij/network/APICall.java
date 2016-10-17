package pl.elfdump.wloczykij.network;

import android.util.Log;
import okhttp3.*;
import pl.elfdump.wloczykij.Session;
import pl.elfdump.wloczykij.exceptions.RequestException;
import pl.elfdump.wloczykij.models.APIError;
import pl.elfdump.wloczykij.utils.JsonUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

public class APICall {

    private Session session;

    public APICall(Session session){
        this.session = session;
    }

    public APIResponse get(String URL, Object... params) throws RequestException{
        return getRequest(String.format(URL, params));
    }

    public APIResponse put(String URL, Object object, Object... params) throws RequestException{
        String json = JsonUtils.serialize(object);
        return put(URL, json, params);
    }

    public APIResponse put(String URL, String json, Object... params) throws RequestException{
        return putRequest(String.format(URL, params), json);
    }

    public APIResponse post(String URL, Object object, Object... params) throws RequestException{
        String json = JsonUtils.serialize(object);
        return post(URL, json, params);
    }

    public APIResponse post(String URL, String json, Object... params) throws RequestException{
        return postRequest(String.format(URL, params), json);
    }


    private APIResponse getRequest(String URL) throws RequestException{
        Request.Builder builder = new Request.Builder()
                .url(getURL(URL));

        return sendRequest(builder);
    }

    private APIResponse putRequest(String URL, String json) throws RequestException{
        Request.Builder builder = new Request.Builder()
                .url(getURL(URL))
                .put(createBody(json));

        return sendRequest(builder);
    }

    private APIResponse postRequest(String URL, String json) throws RequestException{
        Request.Builder builder = new Request.Builder()
                .url(getURL(URL))
                .post(createBody(json));

        return sendRequest(builder);
    }

    private RequestBody createBody(String json){
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(jsonType, json);
    }

    private String getURL(String url){
        URI u;
        try {
            u = new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }

        if(u.isAbsolute()){
            return url;
        }else{
            return APIManager.defaultServer + url;
        }
    }

    public APIResponse sendRequest(Request.Builder builder) throws RequestException{
        OkHttpClient client = new OkHttpClient();

        if(session.authToken != null){
            builder.addHeader("Authorization", "Token " + session.authToken.token);
        }

        Request request = builder.build();

        Response response;
        ResponseBody body;
        try {
            response = client.newCall(request).execute();
            body = response.body();
        }catch (ConnectException e){
            throw new RequestException(0, "Unable to connect to backend");
        }catch (IOException e){
            throw new RequestException(0, "Failed to send API request");
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

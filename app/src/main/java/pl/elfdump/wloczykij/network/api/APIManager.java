package pl.elfdump.wloczykij.network.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;
import pl.elfdump.wloczykij.utils.JsonSerializer;
import pl.elfdump.wloczykij.utils.JsonUtils;

public class APIManager {
    public static class APIError {
        String detail;
        @Override
        public String toString() {
            return detail;
        }
    }

    public static class APIToken {
        String token;
        @Override
        public String toString() {
            return this.token;
        }
    }

    private OkHttpClient httpClient = new OkHttpClient();
    private String serverUrl;
    private String token = null;
    private Map<String, String> endpoints;

    public APIManager(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    private void loadEndpoints() throws APIRequestException {
        if (endpoints != null) return;
        endpoints = sendJsonRequest("GET", this.serverUrl, null, Types.newParameterizedType(Map.class, String.class, String.class));
    }

    public String getEndpointUrl(String name) throws APIRequestException {
        loadEndpoints();
        if (!endpoints.containsKey(name)) throw new APIRequestException("No such endpoint: " + name);
        return endpoints.get(name);
    }

    public String requestToken(@NonNull LoginServiceProvider provider, @NonNull String authToken) throws APIRequestException {
        loadEndpoints();
        APIToken tokenObj = sendJsonRequest("GET", getEndpointUrl("token") + provider.name().toLowerCase() + "/?token=" + authToken, null, APIToken.class);
        this.token = tokenObj.toString();
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private ResponseBody internalSendRequest(String method, String url, @Nullable RequestBody body) throws APIRequestException {
        return internalSendRequest(new Request.Builder().url(url).method(method, body));
    }

    private ResponseBody internalSendRequest(@NonNull Request.Builder builder) throws APIRequestException {
        if (token != null) {
            builder.addHeader("Authorization", "Token " + token);
        }
        Request request = builder.build();

        try {
            Response response = httpClient.newCall(request).execute();
            int responseCode = response.code();

            // Some endpoints return values other than 200 OK (e.g. 201 Created)
            if (responseCode < 200 || responseCode > 299) {
                String responseString = response.body().string();
                String errorMessage;
                try {
                    errorMessage = JsonUtils.deserialize(responseString, APIError.class).toString();
                } catch (JsonDataException e) {
                    errorMessage = responseString;
                } catch (NullPointerException e){
                    errorMessage = "Unknown error";
                }

                if(errorMessage == null){
                    Map<String, List<String>> errors = JsonSerializer.deserialize(responseString, Types.newParameterizedType(
                            Map.class,
                            String.class,
                            Types.newParameterizedType(List.class, String.class)));

                    throw new APIBadRequestException(errors);
                }

                throw new APIRequestException(String.format("The server responded with error %d: %s", responseCode, errorMessage));
            }

            return response.body();
        } catch (ConnectException e) {
            throw new APIRequestException("Unable to connect to backend", e);
        } catch (IOException e) {
            throw new APIRequestException("Failed to send API request", e);
        }
    }

    public String sendRequest(String method, String url, @Nullable RequestBody body) throws APIRequestException {
        try (ResponseBody responseBody = internalSendRequest(method, url, body)) {
            return responseBody.string();
        } catch (IOException e) {
            throw new APIRequestException("Failed to decode server response to string", e);
        }
    }

    public <T> T sendJsonRequest(String method, String url, @Nullable Object body, Type responseType) throws APIRequestException {
        RequestBody requestBody = null;
        if (body != null) {
            String json = JsonUtils.serialize(body);
            requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        }

        String responseJson = sendRequest(method, url, requestBody);

        try {
            return JsonUtils.deserialize(responseJson, responseType);
        } catch (JsonDataException e) {
            throw new APIRequestException("Unable to deserialize JSON response: "+responseJson, e);
        }
    }

    public Map<String, Bitmap> imageCache = new HashMap<>();

    public Bitmap downloadImage(String url) throws APIRequestException {
        if (!imageCache.containsKey(url)) {
            try (ResponseBody responseBody = internalSendRequest("GET", url, null)) {
                imageCache.put(url, BitmapFactory.decodeStream(responseBody.byteStream()));
            }
        }
        return imageCache.get(url);
    }

    public void uploadImage(String url, String mediaType, File file) throws APIRequestException {
        Request.Builder builder = new Request.Builder()
            .url(url)
            .method("POST", RequestBody.create(MediaType.parse(mediaType), file))
            .addHeader("Content-Disposition", "attachment; filename="+file.getName());
        internalSendRequest(builder);
    }

    public Map<Class, APIModelManager> managers = new HashMap<>();

    public <T extends APIModel> APIModelManager<T> manager(Class<T> type) {
        if (!managers.containsKey(type)) {
            managers.put(type, new APIModelManager<>(this, type));
        }
        return managers.get(type);
    }

    /**
     * Shortcut for manager(type).cache()
     */
    public <T extends APIModel> APIModelCache<T> cache(Class<T> type) {
        return manager(type).cache();
    }
}

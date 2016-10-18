package pl.elfdump.wloczykij.network.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.elfdump.wloczykij.network.api.models.APIModel;
import pl.elfdump.wloczykij.network.login.LoginServiceProvider;
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

    public String sendRequest(String method, String url, @Nullable RequestBody body) throws APIRequestException {
        Request.Builder builder = new Request.Builder().url(url).method(method, body);
        if (token != null) {
            builder.addHeader("Authorization", "Token " + token);
        }
        Request request = builder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();

            int responseCode = response.code();
            String responseString = responseBody.string();

            // Some endpoints return values other than 200 OK (e.g. 201 Created)
            if (responseCode < 200 || responseCode > 299) {
                String errorMessage;
                try {
                    errorMessage = JsonUtils.deserialize(responseString, APIError.class).toString();
                } catch (JsonDataException e) {
                    errorMessage = responseString;
                }
                throw new APIRequestException(String.format("The server responded with error %d: %s", responseCode, errorMessage));
            }

            return responseString;
        } catch (ConnectException e) {
            throw new APIRequestException("Unable to connect to backend", e);
        } catch (IOException e) {
            throw new APIRequestException("Failed to send API request", e);
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

    public <T extends APIModel> APIModelManager<T> manager(Class<T> type) {
        // TODO: Cache APIModelManager instances?
        return new APIModelManager<>(this, type);
    }
}

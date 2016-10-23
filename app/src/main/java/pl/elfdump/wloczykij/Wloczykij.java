package pl.elfdump.wloczykij;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.utils.UserSettings;

public class Wloczykij extends Application {
    public static final String TAG = "Wloczykij";

    public static OkHttpClient httpClient = new OkHttpClient();
    public static APIManager api = new APIManager(httpClient, "http://api.wloczykij.eu:8000/");
    public static Session session = new Session(api);
    public static UserSettings settings;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreference = getSharedPreferences("pl.elfdump.wloczykij.USER", Context.MODE_PRIVATE);
        settings = new UserSettings(sharedPreference);
    }
}

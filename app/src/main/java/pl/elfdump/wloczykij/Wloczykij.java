package pl.elfdump.wloczykij;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.utils.UserSettings;

public class Wloczykij extends Application {
    public static final String TAG = "Wloczykij";

    public static APIManager api = new APIManager("http://api.wloczykij.eu:8000/");
    public static Session session = new Session(api);
    public static UserSettings settings;

    private static Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreference = getSharedPreferences("pl.elfdump.wloczykij.USER", Context.MODE_PRIVATE);
        settings = new UserSettings(sharedPreference);
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity){
        currentActivity = activity;
    }
}

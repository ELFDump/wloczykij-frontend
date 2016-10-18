package pl.elfdump.wloczykij;

import android.content.Context;
import android.content.SharedPreferences;

import pl.elfdump.wloczykij.data.DataManager;
import pl.elfdump.wloczykij.data.PlaceStorage;
import pl.elfdump.wloczykij.data.map.MarkerManager;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.utils.UserSettings;

public class Wloczykij {

    public static APIManager api = new APIManager("http://dom.krzysh.pl:8000/");

    private static Session session;

    public static void onStart(Context context){
        session = new Session();

        SharedPreferences sharedPreference = context.getSharedPreferences("pl.elfdump.wloczykij.USER", Context.MODE_PRIVATE);
        session.settings = new UserSettings(sharedPreference);

        DataManager dataManager = new DataManager();
        dataManager.setup();
        session.dataManager = dataManager;
    }

    public static UserSettings getSettings(){
        return session.settings;
    }

    public static Session getSession(){
        return session;
    }

}

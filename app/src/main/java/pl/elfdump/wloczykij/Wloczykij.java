package pl.elfdump.wloczykij;

import android.content.Context;
import android.content.SharedPreferences;
import pl.elfdump.wloczykij.utils.UserSettings;

public class Wloczykij {

    private static Session session;

    public static void onStart(Context context){
        session = new Session();

        SharedPreferences sharedPreference = context.getSharedPreferences("pl.elfdump.wloczykij.USER", Context.MODE_PRIVATE);
        session.settings = new UserSettings(sharedPreference);
    }

    public static UserSettings getSettings(){
        return session.settings;
    }

    public static Session getSession(){
        return session;
    }

}

package pl.elfdump.wloczykij.utils;

import android.content.SharedPreferences;

public class UserSettings {

    private SharedPreferences sharedPreferences;

    public UserSettings(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
    }

    public String getToken(){
        return sharedPreferences.getString("token", null);
    }

    public void setToken(String token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

}

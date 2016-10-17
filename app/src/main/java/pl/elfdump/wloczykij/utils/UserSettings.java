package pl.elfdump.wloczykij.utils;

import android.content.SharedPreferences;
import pl.elfdump.wloczykij.models.Token;

public class UserSettings {

    private SharedPreferences sharedPreferences;

    public UserSettings(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
    }

    public Token getToken(){
        String tokenString = sharedPreferences.getString("token", "");

        if(tokenString.equals("")){
            return null;
        }

        return new Token(tokenString);
    }

    public void setToken(Token token){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token.token);
        editor.apply();
    }

    public boolean tokenExist(){
        return (getToken() != null);
    }

}

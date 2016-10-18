package pl.elfdump.wloczykij.network.login;

import android.content.Intent;

public interface LoginProvider {
    void setUp();

    void logIn();
    void logOut(LoginCallback loginCallback);

    void handleResult(int requestCode, int resultCode, Intent data);
}

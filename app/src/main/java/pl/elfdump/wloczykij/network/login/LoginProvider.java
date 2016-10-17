package pl.elfdump.wloczykij.network.login;

import android.content.Intent;
import pl.elfdump.wloczykij.utils.APICallback;

public interface LoginProvider {
    void setUp();

    void logIn();
    void logOut(APICallback apiCallback);

    void handleResult(int requestCode, int resultCode, Intent data);
}

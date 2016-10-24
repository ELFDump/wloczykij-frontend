package pl.elfdump.wloczykij;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import pl.elfdump.wloczykij.activity.LoginActivity;
import pl.elfdump.wloczykij.network.api.APIBadRequestException;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.network.api.models.Tag;
import pl.elfdump.wloczykij.network.api.models.User;

public class Session {
    private static final int RC_LOGIN = 1111;
    public User loggedOnUser;

    private APIManager api;

    Session(APIManager api) {
        this.api = api;
    }

    public void updateUserData() throws APIRequestException {
        loggedOnUser = api.sendJsonRequest("GET", api.getEndpointUrl("me"), null, User.class);
    }

    public void updateData() throws APIRequestException {
        Wloczykij.api.cache(Tag.class).update();
        Wloczykij.api.cache(Place.class).update();
    }

    public void reloginIfNeeded(Activity activity) {
        if (loggedOnUser == null) {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivityForResult(intent, RC_LOGIN);
        }
    }

    /**
     * Change username
     * @param newUsername New username
     * @return True if changed successfully, false if already taken
     */
    public boolean setUsername(String newUsername) throws APIRequestException {
        User user;
        try {
            user = (User) Wloczykij.session.loggedOnUser.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        user.setUsername(newUsername);

        try {
            loggedOnUser = api.cache(User.class).save(user);
        } catch (APIBadRequestException e) {
            if (e.getErrors().containsKey("username")){
                return false;
            }

            throw e;
        }

        return true;
    }
}

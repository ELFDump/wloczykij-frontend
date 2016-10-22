package pl.elfdump.wloczykij;

import pl.elfdump.wloczykij.network.api.APIBadRequestException;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.User;

public class Session {
    public User loggedOnUser;

    private APIManager api;

    public Session(APIManager api) {
        this.api = api;
    }

    public void updateUserData() throws APIRequestException {
        loggedOnUser = api.sendJsonRequest("GET", api.getEndpointUrl("me"), null, User.class);
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

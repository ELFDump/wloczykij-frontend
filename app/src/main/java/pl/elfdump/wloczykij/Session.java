package pl.elfdump.wloczykij;

import pl.elfdump.wloczykij.network.api.models.User;
import pl.elfdump.wloczykij.data.DataManager;
import pl.elfdump.wloczykij.utils.UserSettings;

public class Session {
    public User loggedOnUser;
    public UserSettings settings;

    public DataManager dataManager;
}

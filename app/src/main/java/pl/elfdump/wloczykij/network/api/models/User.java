package pl.elfdump.wloczykij.network.api.models;

import pl.elfdump.wloczykij.network.api.APIModel;

public class User extends APIModel {
    private String username;
    private String first_name;
    private String last_name;

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }
}

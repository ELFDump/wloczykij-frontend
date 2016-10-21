package pl.elfdump.wloczykij.network.api.models;

import pl.elfdump.wloczykij.network.api.APIModel;

public class User extends APIModel implements Cloneable{
    private String username;
    private String first_name;
    private String last_name;
    private boolean first_login;

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public boolean isFirstLogin(){
        return first_login;
    }

    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

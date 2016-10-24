package pl.elfdump.wloczykij.network.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pl.elfdump.wloczykij.network.api.APIModel;

public class User extends APIModel implements Cloneable{
    private String username;
    private String first_name;
    private String last_name;
    private boolean first_login;
    private List<String> followed_tags = new LinkedList<>();
    private List<String> saved_places = new LinkedList<>();

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

    public List<String> getFollowedTags() {
        return followed_tags;
    }

    public void setFollowedTags(String[] tags){
        this.followed_tags = Arrays.asList(tags);
    }

    public void addFollowedTag(String tag) {
        this.followed_tags.add(tag);
    }

    public void removeFollowedTag(String tag) {
        this.followed_tags.remove(tag);
    }

    public List<String> getSavedPlaces() {
        return saved_places;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

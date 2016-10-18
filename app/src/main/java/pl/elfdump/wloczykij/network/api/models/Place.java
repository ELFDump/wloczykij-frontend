package pl.elfdump.wloczykij.network.api.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Place extends APIModel implements Serializable{
    private String name;
    private String author;
    private String date_created;
    private String date_modified;
    private float[] coords = new float[2]; // TODO: refactor this?
    private String[] photos = new String[0];
    private List<String> tags = new LinkedList<>();

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    // TODO: convert to Date
    public String getDateCreated() {
        return date_created;
    }

    // TODO: convert to Date
    public String getDateModified() {
        return date_modified;
    }

    public float getLat() {
        return coords[0];
    }

    public float getLng() {
        return coords[1];
    }

    public String[] getPhotos() {
        return photos;
    }

    public String[] getTags() {
        return tags.toArray(new String[tags.size()]);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(float lat) {
        this.coords[0] = lat;
    }

    public void setLng(float lng) {
        this.coords[1] = lng;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }
}
package pl.elfdump.wloczykij.network.api.models;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIModel;

public class Place extends APIModel {
    public static class Visit extends APIModel {
        private String date_visited;
        private int rating;

        // TODO: Convert to Date
        public String getDateVisited() {
            return date_visited;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }

    private String name;
    private String description;
    private String author;
    private String date_created;
    private String date_modified;
    private double[] coords = new double[2]; // TODO: refactor this?
    private String[] photos = new String[0];
    private String photo_upload;
    private List<String> tags = new LinkedList<>();
    private String visit_url;
    private Visit visit;
    private Float rating_avg;
    private int rating_count;
    private int visit_count;

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

    public double getLat() {
        return coords[0];
    }

    public double getLng() {
        return coords[1];
    }

    public String[] getPhotos() {
        return photos;
    }

    public String getPhotoUploadUrl() {
        return photo_upload;
    }

    public String[] getTags() {
        return tags.toArray(new String[tags.size()]);
    }

    public void setTags(String[] tags){
        this.tags = Arrays.asList(tags);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(double lat) {
        this.coords[0] = lat;
    }

    public void setLng(double lng) {
        this.coords[1] = lng;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getVisitUrl() {
        return visit_url;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    // TODO: Convert to Date
    public String getDateVisited() {
        if (this.visit == null) return null;
        return this.visit.getDateVisited();
    }

    public int getMyRating() {
        if (this.visit == null) return 0;
        return this.visit.getRating();
    }

    public Float getRatingAverage() {
        return rating_avg;
    }

    public int getRatingCount() {
        return rating_count;
    }

    public int getVisitCount() {
        return visit_count;
    }
}

package pl.elfdump.wloczykij.models;

public class Place {
    private String url;
    private String name;
    private String author;
    private String date_created;
    private String date_modified;
    private float[] coords;
    private String[] photos;
    private String[] tags;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getDate_modified() {
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
        return tags;
    }
}

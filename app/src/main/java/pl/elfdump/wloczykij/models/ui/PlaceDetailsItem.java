package pl.elfdump.wloczykij.models.ui;

public class PlaceDetailsItem {

    private int icon;
    private String title;

    public PlaceDetailsItem(String title) {
        this(-1,title);
    }

    public PlaceDetailsItem(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }

    public int getIcon(){
        return icon;
    }

    public String getTitle(){
        return title;
    }

}

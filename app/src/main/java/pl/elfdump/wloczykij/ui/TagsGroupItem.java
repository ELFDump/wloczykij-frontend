package pl.elfdump.wloczykij.ui;

import java.util.List;

public class TagsGroupItem {

    private String title;
    private List<PlaceDetailsItem> items;

    public TagsGroupItem(String title, List<PlaceDetailsItem> items){
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public List<PlaceDetailsItem> getItems() {
        return items;
    }

}

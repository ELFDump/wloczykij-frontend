package pl.elfdump.wloczykij.ui;

import pl.elfdump.wloczykij.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailsItem {

    public static final int ITEM_GROUP = 0;
    public static final int ITEM_CHILD = 1;

    public List<PlaceDetailsItem> childs = new ArrayList<>();

    private int icon;
    private String title;
    private boolean showCheckBox;
    private int type;
    private boolean isChecked;

    public PlaceDetailsItem(String title) {
        this(R.mipmap.ic_launcher, title);
    }

    public PlaceDetailsItem(int icon, String title, boolean showCheckBox, int type){
        super();
        this.icon = icon;
        this.title = title;
        this.showCheckBox = showCheckBox;
        this.type = type;
    }

    public PlaceDetailsItem(int icon, String title, boolean showCheckBox){
        this(icon, title, showCheckBox, 0);
    }

    public PlaceDetailsItem(int icon, String title) {
        this(icon, title, false, 0);
    }

    public int getIcon(){
        return icon;
    }

    public String getTitle(){
        return title;
    }

    public boolean hasCheckBox(){
        return showCheckBox;
    }

    public int getType(){
        return type;
    }

    public void setType(int state){
        this.type = state;
    }

    public boolean isChecked(){
        return isChecked;
    }

    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

}

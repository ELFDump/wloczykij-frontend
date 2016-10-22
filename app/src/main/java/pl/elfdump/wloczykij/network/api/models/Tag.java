package pl.elfdump.wloczykij.network.api.models;

import android.support.annotation.Nullable;

import pl.elfdump.wloczykij.network.api.APIModel;

public class Tag extends APIModel {
    private String name;
    private int place_count;

    @Nullable
    @Override
    public String getId() {
        return getName();
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlaceCount() {
        return place_count;
    }

    public void setPlaceCount(int place_count) {
        this.place_count = place_count;
    }
}

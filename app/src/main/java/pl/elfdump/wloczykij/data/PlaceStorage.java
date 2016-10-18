package pl.elfdump.wloczykij.data;

import pl.elfdump.wloczykij.models.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceStorage {

    private List<Place> places = new ArrayList<>();

    public void put(Place place){
        places.add(place);
    }

    public List<Place> getPlaces(){
        return places;
    }

    public void setPlaces(List<Place> places){
        this.places = places;
    }

}

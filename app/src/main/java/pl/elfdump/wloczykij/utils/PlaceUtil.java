package pl.elfdump.wloczykij.utils;

import pl.elfdump.wloczykij.network.api.models.Place;

import java.util.*;

public class PlaceUtil {

    public static List<Place> filterPlaces(Collection<Place> places, String[] tags){
        List<Place> filtered = new ArrayList<>();

        for(Place p : places){
            // Collections.disjoint checks if there are no common values in lists
            // So we invert the result
            if(!Collections.disjoint(Arrays.asList(p.getTags()), Arrays.asList(tags))){
                filtered.add(p);
            }
        }

        return filtered;
    }

}

package pl.elfdump.wloczykij.planner;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.utils.MapUtil;
import pl.elfdump.wloczykij.utils.Util;

public class PlacePlanner {
    private LatLng startPoint;
    private Collection<Place> places;
    private Collection<String> includedTags = new LinkedList<>();

    public PlacePlanner setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
        return this;
    }

    public PlacePlanner setPlaces(Collection<Place> places) {
        this.places = places;
        return this;
    }

    public PlacePlanner setIncludedTags(Collection<String> includedTags) {
        this.includedTags = includedTags;
        return this;
    }

    public PlacePlanner addIncludedTag(String includedTag) {
        this.includedTags.add(includedTag);
        return this;
    }

    public List<Place> findPlaces(int amount) {
        // TODO: Think about this algorithm, it probably requires a bit of tweaking
        Map<Place, Float> selectedPlaces = new HashMap<>();
        for (Place place : places) {
            float matchScore = 0;
            for (String tag : place.getTags()) {
                if (includedTags.contains(tag)) {
                    matchScore ++;
                }
            }
            matchScore -= MapUtil.getDistance(startPoint, MapUtil.getPosition(place))/3000;
            //System.out.println("Score for "+place.toString()+" is "+matchScore);
            if (matchScore > 0) {
                selectedPlaces.put(place, matchScore);
            }
        }

        List<Place> selectedPlacesList = Util.sortMapKeysByValues(selectedPlaces);
        if (selectedPlacesList.size() <= amount) {
            return selectedPlacesList;
        } else {
            return new ArrayList<>(selectedPlacesList.subList(0, amount+1));
        }
    }
}

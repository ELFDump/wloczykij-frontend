package pl.elfdump.wloczykij.utils;

import com.google.android.gms.maps.model.LatLng;
import pl.elfdump.wloczykij.models.Place;

public class MapUtilities {

    public static LatLng getPosition(Place place){
        return new LatLng(place.getLat(), place.getLng());
    }

}

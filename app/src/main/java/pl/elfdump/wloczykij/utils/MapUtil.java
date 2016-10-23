package pl.elfdump.wloczykij.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pl.elfdump.wloczykij.network.api.models.Place;

public class MapUtil {

    public static LatLng getPosition(Place place){
        return new LatLng(place.getLat(), place.getLng());
    }

    // http://wptrafficanalyzer.in/blog/route-between-two-locations-with-waypoints-in-google-map-android-api-v2/
    public static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}

package pl.elfdump.wloczykij.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pl.elfdump.wloczykij.network.api.models.Place;

public class MapUtil {

    public static LatLng getPosition(Place place){
        return new LatLng(place.getLat(), place.getLng());
    }

    public static double getDistance(LatLng p1, LatLng p2) {
        // TODO: You cannot use this in unit tests :/
        // float[] results = new float[1];
        // Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results);
        // return results[0];
        double l1 = Math.toRadians(p1.latitude);
        double l2 = Math.toRadians(p2.latitude);
        double g1 = Math.toRadians(p1.longitude);
        double g2 = Math.toRadians(p2.longitude);

        double dist = Math.acos(Math.sin(l1) * Math.sin(l2) + Math.cos(l1) * Math.cos(l2) * Math.cos(g1 - g2));
        if (dist < 0)
            dist = dist + Math.PI;

        return dist * 6378100;
    }

    // http://wptrafficanalyzer.in/blog/route-between-two-locations-with-waypoints-in-google-map-android-api-v2/
    public static ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
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

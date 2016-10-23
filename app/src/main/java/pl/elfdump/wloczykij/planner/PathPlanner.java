package pl.elfdump.wloczykij.planner;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.utils.MapUtil;

public class PathPlanner {
    private static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json";

    private final OkHttpClient httpClient;
    private final String mapsKey;

    private LatLng startPoint;
    private List<Place> pathPlaces = new LinkedList<>();

    public class PlannedPath {
        private final Place[] placeList;
        private final List<LatLng> line;

        PlannedPath(Place[] placeList, List<LatLng> line) {
            this.placeList = placeList;
            this.line = line;
        }

        public Place[] getPlaceList() {
            return placeList;
        }

        public List<LatLng> getLine() {
            return line;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PlannedPath\n");
            sb.append("  placeList:\n");
            for (Place place : placeList)
                sb.append("    " + place.toString() + "\n");
            sb.append("  line:\n");
            for (LatLng linePoint : line)
                sb.append("    " + linePoint.toString() + "\n");
            return sb.toString();
        }
    }

    public PathPlanner(OkHttpClient httpClient, String mapsKey) {
        this.httpClient = httpClient;
        this.mapsKey = mapsKey;
    }

    public PathPlanner setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
        return this;
    }

    public PathPlanner addPathPlace(Place place) {
        this.pathPlaces.add(place);
        return this;
    }

    private String encodeLatLng(double lat, double lng) {
        return lat + "," + lng;
    }

    private String encodeLatLng(LatLng latlng) {
        return encodeLatLng(latlng.latitude, latlng.longitude);
    }

    public PlannedPath plan() throws IOException, JSONException {
        if (startPoint == null)
            throw new IllegalArgumentException("startPoint not set");
        if (pathPlaces.size() == 0)
            throw new IllegalArgumentException("No pathPlaces added");

        String waypoints = "optimize:true";
        for (Place place : pathPlaces) {
            waypoints += "|" + encodeLatLng(MapUtil.getPosition(place));
        }

        HttpUrl url = HttpUrl.parse(DIRECTIONS_API_URL).newBuilder()
            .addQueryParameter("origin", encodeLatLng(startPoint))
            .addQueryParameter("destination", encodeLatLng(startPoint))
            .addQueryParameter("waypoints", waypoints)
            .addQueryParameter("key", mapsKey)
            .build();

        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) throw new IOException("HTTP error " + response.code());
            JSONObject responseJson = new JSONObject(response.body().string());
            JSONObject route = responseJson.getJSONArray("routes").getJSONObject(0);
            List<LatLng> line = MapUtil.decodePoly(route.getJSONObject("overview_polyline").getString("points"));
            JSONArray waypointOrder = route.getJSONArray("waypoint_order");
            Place[] placeList = new Place[waypointOrder.length()];
            for(int i = 0; i < waypointOrder.length(); i++) {
                placeList[i] = pathPlaces.get(waypointOrder.getInt(i));
            }
            return new PlannedPath(placeList, line);
        }
    }
}

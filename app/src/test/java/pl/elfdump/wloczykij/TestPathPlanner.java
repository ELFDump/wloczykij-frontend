package pl.elfdump.wloczykij;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.planner.PathPlanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPathPlanner {
    private OkHttpClient httpClient;

    @Before
    public void setUp() {
        httpClient = new OkHttpClient();
    }

    @After
    public void tearDown() {
        httpClient = null;
    }

    @Test
    public void testPathPlanner() throws IOException, JSONException {
        LatLng startPoint = new LatLng(51.759248, 19.455983);

        Place pokojDamiana = new Place()
            .setName("Pokój Damiana")
            .setLat(51.739645)
            .setLng(19.486432);

        Place test = new Place()
            .setName("test")
            .setLat(51.729303)
            .setLng(19.486769);

        PathPlanner.PlannedPath path = new PathPlanner(httpClient, "AIzaSyAoFjPXQuPGrPPdDweh1giyooctH6olD14")
            .setStartPoint(startPoint)
            .addPathPlace(pokojDamiana)
            .addPathPlace(test)
            .plan();

        System.out.println(path);

        assertNotNull(path);
        assertEquals(path.getPlaceList().length, 2);
    }
}

package pl.elfdump.wloczykij;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import pl.elfdump.wloczykij.network.api.models.Place;
import pl.elfdump.wloczykij.planner.PlacePlanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPlacePlanner {
    @Test
    public void testPlacePlanner() {
        LatLng startPoint = new LatLng(51.733562, 19.486094);

        List<Place> places = new LinkedList<>();
        places.add(new Place()
            .setName("Pokój Damiana")
            .setLat(51.739645)
            .setLng(19.486432)
            .addTag("Zabytek")
        );
        places.add(new Place()
            .setName("test")
            .setLat(51.729303)
            .setLng(19.486769)
            .addTag("Jedzenie")
        );

        List<Place> selectedPlaces = new PlacePlanner()
            .setPlaces(places)
            .addIncludedTag("Zabytek")
            .setStartPoint(startPoint)
            .findPlaces();

        assertNotNull(selectedPlaces);
        assertEquals(1, selectedPlaces.size());
        assertEquals("Pokój Damiana", selectedPlaces.get(0).getName());
    }
}

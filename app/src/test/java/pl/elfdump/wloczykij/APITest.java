package pl.elfdump.wloczykij;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import okhttp3.OkHttpClient;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;

import static org.junit.Assert.assertNotNull;

public class APITest {
    private static final String TEST_SERVER = "http://127.0.0.1:8000/";
    private APIManager api;

    @Before
    public void setUp() {
        api = new APIManager(new OkHttpClient(), TEST_SERVER);
    }

    @After
    public void tearDown() {
        api = null;
    }

    @Test
    public void APIModelManager_getAll_Place() throws APIRequestException {
        Collection<Place> places = api.manager(Place.class).getAll();
        assertNotNull(places);
        for (Place place : places) {
            assertNotNull(place.getResourceUrl());
            assertNotNull(place.getId());
            assertNotNull(place.getName());
        }
    }

    @Test
    public void APIModelCache_getAll_Place() throws APIRequestException {
        api.cache(Place.class).update();
        Collection<Place> places = api.cache(Place.class).getAll();
        assertNotNull(places);
        for (Place place : places) {
            assertNotNull(place.getResourceUrl());
            assertNotNull(place.getId());
            assertNotNull(place.getName());
        }
    }
}
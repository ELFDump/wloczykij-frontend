package pl.elfdump.wloczykij.data;

import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.data.map.MarkerManager;
import pl.elfdump.wloczykij.models.Place;
import pl.elfdump.wloczykij.network.tasks.UpdatePlacesTask;

import java.util.List;

public class DataManager {

    public PlaceStorage placeStorage;
    public MarkerManager markerManager;

    public void setup(){
        placeStorage = new PlaceStorage();
        markerManager = new MarkerManager();

        new UpdatePlacesTask(Wloczykij.api) {
            @Override
            protected void onPostExecute(List<Place> places) {
                placeStorage.setPlaces(places);
            }
        }.execute();
    }

}

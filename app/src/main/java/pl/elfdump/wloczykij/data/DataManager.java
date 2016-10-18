package pl.elfdump.wloczykij.data;

import android.os.AsyncTask;
import android.util.Log;
import pl.elfdump.wloczykij.Wloczykij;
import pl.elfdump.wloczykij.data.map.MarkerManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;
import pl.elfdump.wloczykij.network.api.models.Place;

import java.util.List;

public class DataManager {

    public PlaceStorage placeStorage;
    public MarkerManager markerManager;

    public void setup(){
        placeStorage = new PlaceStorage();
        markerManager = new MarkerManager();

        new AsyncTask<Void, Void, List<Place>>() {
            @Override
            protected List<Place> doInBackground(Void... params) {
                try {
                    return Wloczykij.api.manager(Place.class).getAll();
                } catch (APIRequestException e) {
                    // TODO: handle errors
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Place> places) {
                placeStorage.setPlaces(places);
            }
        }.execute();
    }

}

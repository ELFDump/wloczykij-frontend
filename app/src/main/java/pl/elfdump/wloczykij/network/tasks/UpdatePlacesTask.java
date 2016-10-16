package pl.elfdump.wloczykij.network.tasks;

import android.os.AsyncTask;

import java.util.List;

import pl.elfdump.wloczykij.models.Place;
import pl.elfdump.wloczykij.network.api.PlaceService;

public class UpdatePlacesTask extends AsyncTask<Void, Void, List<Place>> {
    @Override
    protected List<Place> doInBackground(Void... params) {
        return new PlaceService().getPlaces();
    }
}
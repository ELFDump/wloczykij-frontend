package pl.elfdump.wloczykij.network.tasks;

import android.os.AsyncTask;

import com.squareup.moshi.Types;

import java.util.List;

import pl.elfdump.wloczykij.models.Place;
import pl.elfdump.wloczykij.network.api.APIManager;
import pl.elfdump.wloczykij.network.api.APIRequestException;

public class UpdatePlacesTask extends AsyncTask<Void, Void, List<Place>> {
    private APIManager api;

    public UpdatePlacesTask(APIManager api) {
        this.api = api;
    }

    @Override
    protected List<Place> doInBackground(Void... params) {
        try {
            return api.sendJsonRequest("GET", api.getEndpointUrl("places"), null, Types.newParameterizedType(List.class, Place.class));
        } catch (APIRequestException e) {
            e.printStackTrace();
            return null;
        }
    }
}
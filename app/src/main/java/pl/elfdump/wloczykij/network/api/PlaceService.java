package pl.elfdump.wloczykij.network.api;

import android.util.Log;
import pl.elfdump.wloczykij.exceptions.RequestException;
import pl.elfdump.wloczykij.models.User;
import pl.elfdump.wloczykij.network.APIManager;
import pl.elfdump.wloczykij.network.APIResponse;
import pl.elfdump.wloczykij.utils.JsonUtils;

public class PlaceService {

    public List<Place> getPlace(){
        APIResponse response;
        try {
            response = APIManager.newCall().get("/places/");
        } catch (RequestException e) {
            Log.e(APIManager.TAG, e.getMessage());
            return null;
        }

        return JsonUtils.deserialize(response.json(), Types.newParameterizedType(List.class, Place.class));
    }


}

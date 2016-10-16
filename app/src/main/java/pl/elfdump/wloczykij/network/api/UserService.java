package pl.elfdump.wloczykij.network.api;

import android.util.Log;
import pl.elfdump.wloczykij.exceptions.RequestException;
import pl.elfdump.wloczykij.models.User;
import pl.elfdump.wloczykij.network.APIManager;
import pl.elfdump.wloczykij.network.APIResponse;
import pl.elfdump.wloczykij.utils.JsonUtils;

public class UserService {

    public User getMe(){
        APIResponse response;
        try {
            response = APIManager.newCall().get("/me/");
        } catch (RequestException e) {
            Log.e(APIManager.TAG, e.getMessage());
            return null;
        }

        return JsonUtils.deserialize(response.json(), User.class);
    }

    public User getUser(int id){
        APIResponse response;
        try {
            response = APIManager.newCall().get("/users/%d/", id);
        } catch (RequestException e) {
            Log.e(APIManager.TAG, e.getMessage());
            return null;
        }

        return JsonUtils.deserialize(response.json(), User.class);
    }

}

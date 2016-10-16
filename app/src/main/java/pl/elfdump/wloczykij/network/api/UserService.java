package pl.elfdump.wloczykij.network.api;

import android.util.Log;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import pl.elfdump.wloczykij.models.User;
import pl.elfdump.wloczykij.network.APICall;
import pl.elfdump.wloczykij.network.APIResponse;

import java.io.IOException;

public class UserService {

    public User getMe(){
        APIResponse response = APICall.request("/me/");

        if(response.code() != 200){
            return null;
        }

        return deserializeUser(response.json());
    }

    public User getUser(int id){
        APIResponse response = APICall.request("/users/%d/", id);

        if(response.code() != 200){
            return null;
        }

        return deserializeUser(response.json());
    }

    private User deserializeUser(String json){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);

        User user = null;
        try {
            user = jsonAdapter.fromJson(json);
        }catch (IOException e){
            Log.e(APICall.TAG, "Failed to deserialize User", e);
        }

        return user;
    }

}

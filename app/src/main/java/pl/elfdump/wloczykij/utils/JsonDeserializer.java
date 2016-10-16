package pl.elfdump.wloczykij.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

class JsonDeserializer{

    protected static <T> T deserialize(String json, Class<T> clazz){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> jsonAdapter;
        Object result = null;

        jsonAdapter = moshi.adapter(clazz);
        try {
            result = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (T) result;
    }

}

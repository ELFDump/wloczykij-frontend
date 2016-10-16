package pl.elfdump.wloczykij.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

class JsonSerializer {

    protected static String serialize(Object object){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter jsonAdapter = moshi.adapter(object.getClass());

        return jsonAdapter.toJson(object);
    }

    protected static <T> T deserialize(String json, Class<T> clazz){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<T> jsonAdapter = moshi.adapter(clazz);
        Object result = null;

        try {
            result = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (T) result;
    }

}

package pl.elfdump.wloczykij.utils;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class JsonSerializer {

    public static String serialize(Object object){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter jsonAdapter = moshi.adapter(object.getClass());

        return jsonAdapter.toJson(object);
    }

    public static <T> T deserialize(String json, Type clazz){
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

package pl.elfdump.wloczykij.utils;

public class JsonUtils {

    public static <T> T deserialize(String json, Class<T> clazz){
        return JsonSerializer.deserialize(json, clazz);
    }

    public static String serialize(Object object){
        return JsonSerializer.serialize(object);
    }

}

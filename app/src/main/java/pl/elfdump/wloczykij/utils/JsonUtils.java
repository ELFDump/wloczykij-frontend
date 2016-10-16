package pl.elfdump.wloczykij.utils;

public class JsonUtils {

    public static <T> T deserialize(String json, Class<T> clazz){
        return JsonDeserializer.deserialize(json, clazz);
    }

}

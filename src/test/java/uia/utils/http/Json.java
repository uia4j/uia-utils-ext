package uia.utils.http;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {

    @SuppressWarnings("rawtypes")
    public static Map toMap(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, Map.class);
    }

    public static String toString(Object value) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(value);
    }

    public static String format(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(gson.fromJson(json, Map.class));
    }
}

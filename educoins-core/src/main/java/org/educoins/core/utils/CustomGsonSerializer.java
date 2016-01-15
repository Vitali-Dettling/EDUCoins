package org.educoins.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Custom Gson Serializer used for types where GSON (de-)serialization is not optimal.
 * Created by dacki on 15.01.16.
 */
public class CustomGsonSerializer {
    /**
     * returns a {@link Gson} object with custom adapters for different Classes
     * @return
     */
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Sha256Hash.class, Sha256JsonWriter.getInstance());
        gsonBuilder.registerTypeAdapter(Sha256Hash.class, Sha256JsonReader.getInstance());
        return gsonBuilder.create();
    }
}

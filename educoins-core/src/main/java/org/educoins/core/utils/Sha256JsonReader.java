package org.educoins.core.utils;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Custom Json reader that can be registered with GSON for custom deserialization
 * Created by dacki on 15.01.16.
 */
public class Sha256JsonReader implements JsonDeserializer<Sha256Hash> {

    public static Sha256JsonReader getInstance() {
        return new Sha256JsonReader();
    }

    @Override
    public Sha256Hash deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Sha256Hash.wrap(json.getAsString());
    }
}

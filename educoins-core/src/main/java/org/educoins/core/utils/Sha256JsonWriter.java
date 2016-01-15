package org.educoins.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Custom Json writer that can be registered with GSON for custom serialization
 * Created by dacki on 15.01.16.
 */
public class Sha256JsonWriter implements JsonSerializer<Sha256Hash> {

    public static Sha256JsonWriter getInstance() {
        return new Sha256JsonWriter();
    }

    @Override
    public JsonElement serialize(Sha256Hash hash, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(hash.toString());
    }
}

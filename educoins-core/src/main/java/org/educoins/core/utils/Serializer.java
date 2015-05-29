package org.educoins.core.utils;

import org.educoins.core.Block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Serializer {

	private static final Gson GSON_SERIALIZER = new GsonBuilder().setPrettyPrinting().create();

	public static String serialize(Block block) {
		try {
			String jsonString = GSON_SERIALIZER.toJson(block);
			return jsonString;
		} catch (Exception ex) {
			System.out.println("Serializer.serialize: Error while serialize block");
			return null;
		}
	}

}

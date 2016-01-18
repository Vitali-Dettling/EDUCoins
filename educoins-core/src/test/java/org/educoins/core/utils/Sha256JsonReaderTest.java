package org.educoins.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.educoins.core.Block;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dacki on 15.01.16.
 */
public class Sha256JsonReaderTest {
    private static Sha256JsonReader reader;
    private static Gson gson;

    @BeforeClass
    public static void setup() {
        reader = Sha256JsonReader.getInstance();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Sha256Hash.class, reader);
        gson = gb.create();
    }

    @Test
    public void testDeserialize() throws Exception {
        StringBuilder jsonString = new StringBuilder(66);
        jsonString.append("\"");
        jsonString.append(Sha256Hash.ZERO_HASH.toString());
        jsonString.append("\"");

        Sha256Hash hash = gson.fromJson(jsonString.toString(), Sha256Hash.class);

        assertEquals(Sha256Hash.ZERO_HASH, hash);
    }

    /**
     * Testing a default Block to see if its elements arebeing serialized correctly
     * @throws Exception
     */
    @Test
    public void testDeserializeBlock() throws Exception {
        Block expected = new Block();
        String jsonString = "{\"version\":1,\"hashPrevBlock\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"hashMerkleRoot\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"time\":0,\"bits\":[29,0,-1,-1],\"nonce\":1114735442,\"transactions\":[]}";
        Block block = gson.fromJson(jsonString, Block.class);
        // Actual String maight differ, when default values or the Block itself changes.
        // Adapt by either setting values explicitly or adapt the string
        assertEquals(expected, block);
    }
}
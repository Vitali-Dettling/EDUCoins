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
public class Sha256JsonWriterTest {

    private static Sha256JsonWriter writer;
    private static Gson gson;

    @BeforeClass
    public static void setup() {
        writer = Sha256JsonWriter.getInstance();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(Sha256Hash.class, writer);
        gson = gb.create();
    }

    @Test
    public void testSerialize() throws Exception {
        Sha256Hash hash = Sha256Hash.ZERO_HASH;
        String json = gson.toJson(hash);

        StringBuilder expected = new StringBuilder(66);
        expected.append("\"");
        expected.append(Sha256Hash.ZERO_HASH.toString());
        expected.append("\"");

        assertEquals(expected.toString(), json);
    }

    /**
     * Testing a default Block to see if its elements arebeing serialized correctly
     * @throws Exception
     */
    @Test
    public void testSerializeBlock() throws Exception {
        Block block = new Block();
        String json = gson.toJson(block);
        String expected = "{\"version\":1,\"hashPrevBlock\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"hashMerkleRoot\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"time\":0,\"bits\":[29,0,-1,-1],\"nonce\":1114735442,\"transactions\":[]}";
        // Actual String maight differ, when default values or the Block itself changes.
        // Adapt by either setting values explicitly or adapt the string
        assertEquals(expected, json);
    }
}
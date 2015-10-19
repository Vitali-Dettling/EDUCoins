package org.educoins.core.store;

import com.google.gson.Gson;
import org.educoins.core.Block;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

/**
 * The default implementation of a {@link BlockStore} using Google Level Db as storage-backend.
 * Created by typus on 10/18/15.
 */
public class LevelDbBlockStore implements BlockStore {

    private final byte[] anchor = "anchor".getBytes();
    private final File path;
    private DB database;

    public LevelDbBlockStore(File directory, DBFactory dbFactory) throws BlockStoreException {
        this.path = directory;
        Options options = new Options();
        options.createIfMissing();

        try {
            tryOpen(directory, dbFactory, options);
        } catch (IOException e) {
            try {
                dbFactory.repair(directory, options);
                tryOpen(directory, dbFactory, options);
            } catch (IOException e1) {
                throw new BlockStoreException(e1);
            }
        }
    }

    private synchronized void tryOpen(File directory, DBFactory dbFactory, Options options) throws IOException, BlockStoreException {
        database = dbFactory.open(directory, options);
        initStoreIfNeeded();
    }

    private void initStoreIfNeeded() {
        // Store already up.
        if (database.get(anchor) != null)
            return;
        addGenesisBlock();
    }

    private void addGenesisBlock() {
        //TODO: think about + implement
    }

    @Override
    public void put(Block block) {
        //TODO: Merkle Root the right one?!
        database.put(block.getHashMerkleRoot().getBytes(), getJson(block).getBytes());
    }


    @Override
    public Block get(String hash) {
        return getBlock(new String(database.get(hash.getBytes())));
    }

    @Override
    public void destroy() throws BlockStoreException {
        try {
            database.close();
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    private String getJson(Block block) {
        return new Gson().toJson(block);
    }

    private Block getBlock(String jsonblock) {
        return new Gson().fromJson(jsonblock, Block.class);
    }
}

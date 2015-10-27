package org.educoins.core.store;

import com.google.gson.Gson;
import com.sun.istack.internal.Nullable;
import org.educoins.core.Block;
import org.educoins.core.utils.ByteArray;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * The default implementation of a {@link IBlockStore} using Google Level Db as storage-backend.
 * Created by typus on 10/18/15.
 */
public class LevelDbBlockStore implements IBlockStore {

    private final byte[] LATEST_KEY = "latest".getBytes();

    private final File path;

    private DB database;
    private byte[] latest;

    public LevelDbBlockStore(File directory) throws BlockStoreException {
        DBFactory dbFactory = JniDBFactory.factory;

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

    private synchronized void tryOpen(File directory, DBFactory dbFactory, Options options)
            throws IOException, BlockStoreException {
        database = dbFactory.open(directory, options);
    }


    @Override
    public synchronized void put(@NotNull Block block) {
//        TODO: would be a lot nicer?! byte[] key = Block.hash(block);
        byte[] key = ByteArray.convertToString(block.hash(), 16).getBytes();

        database.put(key, getJson(block).getBytes());
        latest = key;
        database.put(LATEST_KEY, latest);
    }


    @Override
    @Nullable
    public synchronized Block get(byte[] hash) throws BlockNotFoundException {
        if (database.get(hash) == null) {
            throw new BlockNotFoundException(hash);
        }
        return getBlock(database.get(hash));
    }

    @Override
    @Nullable
    public synchronized Block getLatest() {
        if (isEmpty()) return null;

        try {
            return getBlock(database.get(latest));
        } catch (DBException ex) {
            return null;
        }
    }

    private boolean isEmpty() {
        if (latest == null)
            latest = database.get(LATEST_KEY);

        return latest == null;
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

    private Block getBlock(byte[] jsonblock) {
        return new Gson().fromJson(new String(jsonblock), Block.class);
    }
}

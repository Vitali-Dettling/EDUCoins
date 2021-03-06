package org.educoins.core.store;

import com.google.gson.Gson;
import org.educoins.core.Block;
import org.educoins.core.utils.IO;
import org.educoins.core.utils.Sha256Hash;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * The default implementation of a {@link IBlockStore} using Google Level Db as storage-backend.
 * Created by typus on 10/18/15.
 */
public class LevelDbBlockStore implements IBlockStore {

    private final byte[] LATEST_KEY = "latest".getBytes();

    private byte[] genesisHash = null;

    private DB database;
    private byte[] latest;

    public LevelDbBlockStore() throws BlockStoreException {
        this(IO.getDefaultBlockStoreFile());
    }

    public LevelDbBlockStore(File directory) throws BlockStoreException {
        DBFactory dbFactory = JniDBFactory.factory;

        Options options = new Options();
        options.createIfMissing();

        try {
            tryOpen(directory, dbFactory, options);
            Block block = new Block();
            byte[] hash = block.hash().getBytes();
            if (genesisHash == null) {
                genesisHash = hash;
            }

            database.put(hash, getJson(block).getBytes());
            latest = block.hash().getBytes();
            database.put(LATEST_KEY, latest);

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
            throws IOException {
        database = dbFactory.open(directory, options);
    }


    @Override
    public synchronized void put(@NotNull Block block) {
        byte[] hash = block.hash().getBytes();
        if (genesisHash == null) {
            genesisHash = hash;
        }

        database.put(hash, getJson(block).getBytes());
        latest = block.hash().getBytes();
        database.put(LATEST_KEY, latest);
    }

    @Override
    @NotNull
    public synchronized Block get(Sha256Hash hash) throws BlockNotFoundException {
        byte[] byteBlock = database.get(hash.getBytes());

        if (byteBlock == null)
            throw new BlockNotFoundException(hash.toString());


        return getBlock(byteBlock);
    }

    @SuppressWarnings("restriction")
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

    @Override
    public void destroy() throws BlockStoreException {
        try {
            database.close();
        } catch (IOException e) {
            throw new BlockStoreException(e);
        }
    }

    @Override
    public IBlockIterator iterator() {
        return new BlockIterator(this, genesisHash);
    }

    private boolean isEmpty() {
        if (latest == null)
            latest = database.get(LATEST_KEY);

        return latest == null;
    }

    private String getJson(Block block) {
        return new Gson().toJson(block);
    }

    private Block getBlock(byte[] jsonblock) {
        Block block;
        try {
            block = new Gson().fromJson(new String(jsonblock), Block.class);
        } catch (Exception e) {
            System.err.println("Not a block object, why is it stored in the DB?");
            block = null;
        }
        return block;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.destroy();
    }
}

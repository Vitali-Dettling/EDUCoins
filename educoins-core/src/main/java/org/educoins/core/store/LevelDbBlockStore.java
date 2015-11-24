package org.educoins.core.store;

import java.io.File;
import java.io.IOException;

import org.educoins.core.Block;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;

/**
 * The default implementation of a {@link IBlockStore} using Google Level Db as storage-backend.
 * Created by typus on 10/18/15.
 */
public class LevelDbBlockStore implements IBlockStore {

    private final byte[] LATEST_KEY = "latest".getBytes();

    private byte[] genesisHash = null;

    private DB database;
    private byte[] latest;

    public LevelDbBlockStore(File directory) throws BlockStoreException {
        DBFactory dbFactory = JniDBFactory.factory;

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
    public synchronized Block get(byte[] hash) {
        byte[] byteBlock = database.get(hash);

        if (byteBlock == null) {
            try {
				throw new BlockNotFoundException(hash);
			} catch (BlockNotFoundException e) {
				e.printStackTrace();
			}
        }
        return getBlock(byteBlock);
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

    @Override
    public void destroy() throws BlockStoreException  {
        try {
            database.close();
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBlockIterator blockIterator() {
        return new BlockIterator(this, genesisHash);
    }
    
	@Override
	public ITransactionIterator transactionIterator() {
		return new TransactionIterator(this, genesisHash);
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
    	Block block = null;
    	try{
    		block = new Gson().fromJson(new String(jsonblock), Block.class);
    	}catch(Exception e){
    		System.err.println("Not a block object, why is it stored in the DB?");
    		block = null;
    	}
        return block;
    }
}

package org.educoins.core.store;

import org.educoins.core.Block;

/**
 * The Exception thrown if a {@link Block} could not be found.
 * Created by typus on 10/19/15.
 */
public class BlockNotFoundException extends BlockStoreException {
    public BlockNotFoundException() {
    }

    public BlockNotFoundException(byte[] hash) {
        super("Block with Hash '" + new String(hash) + "' could not be found!");
    }

    public BlockNotFoundException(String message) {
        super(message);
    }

    public BlockNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockNotFoundException(Throwable cause) {
        super(cause);
    }

    public BlockNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

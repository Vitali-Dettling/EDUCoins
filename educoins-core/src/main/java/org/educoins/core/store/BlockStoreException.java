package org.educoins.core.store;

/**
 * The Exception thrown by a  {@link BlockStore}.
 * Created by typus on 10/18/15.
 */
public class BlockStoreException extends RuntimeException {
    public BlockStoreException() {
    }

    public BlockStoreException(String message) {
        super(message);
    }

    public BlockStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockStoreException(Throwable cause) {
        super(cause);
    }

    public BlockStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

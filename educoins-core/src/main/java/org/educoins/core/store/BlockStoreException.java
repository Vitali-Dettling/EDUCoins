package org.educoins.core.store;

import java.io.IOException;

/**
 * The Exception thrown by a  {@link IBlockStore}.
 * Created by typus on 10/18/15.
 */
public class BlockStoreException extends IOException {
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
}

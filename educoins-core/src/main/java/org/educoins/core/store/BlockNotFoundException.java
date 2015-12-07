package org.educoins.core.store;

import org.educoins.core.Block;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Exception thrown if a {@link Block} could not be found.
 * Created by typus on 10/19/15.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Block")
public class BlockNotFoundException extends BlockStoreException {
    public BlockNotFoundException() {
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

    public BlockNotFoundException(byte[] hash) {
        super("Block with Hash '" + new String(hash) + "' could not be found!");
    }
}

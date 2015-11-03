package org.educoins.core.p2p.discovery;

import java.io.IOException;

/**
 * The Exception thrown by {@link DiscoveryStrategy}s.
 * Created by typus on 11/3/15.
 */
public class DiscoveryException extends IOException {

    public DiscoveryException() {
    }

    public DiscoveryException(String message) {
        super(message);
    }

    public DiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscoveryException(Throwable cause) {
        super(cause);
    }

}

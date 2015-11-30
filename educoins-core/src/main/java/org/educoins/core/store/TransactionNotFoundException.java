package org.educoins.core.store;

import java.io.IOException;

public class TransactionNotFoundException extends IOException {
    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionNotFoundException(Throwable cause) {
        super(cause);
    }
}

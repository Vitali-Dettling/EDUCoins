package org.educoins.core.utils;

import java.io.IOException;

/**
 * Thrown by {@link RestClient} in error-cases.
 * Created by typus on 11/3/15.
 */
public class HttpException extends IOException {

    private final int status;

    public HttpException(int status) {
        this.status = status;
    }

    public HttpException(String message, int status) {
        super(message);
        this.status = status;
    }

    public HttpException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public HttpException(Throwable cause, int status) {
        super(cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

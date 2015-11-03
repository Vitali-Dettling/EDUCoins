package org.educoins.core.utils;

import java.io.IOException;

/**
 * Thrown by {@link RestClient} in error-cases.
 * Created by typus on 11/3/15.
 */
public class HttpException extends IOException {

    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

}

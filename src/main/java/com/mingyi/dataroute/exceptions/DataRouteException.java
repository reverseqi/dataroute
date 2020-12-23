package com.mingyi.dataroute.exceptions;

/**
 *
 * @author vbrug
 * @since 1.0.0
 */
public class DataRouteException extends RuntimeException {

    public DataRouteException() {
        super();
    }

    public DataRouteException(String message) {
        super(message);
    }

    public DataRouteException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataRouteException(Throwable cause) {
        super(cause);
    }
}

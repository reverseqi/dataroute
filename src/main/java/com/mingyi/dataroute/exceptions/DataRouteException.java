package com.mingyi.dataroute.exceptions;

import com.vbrug.fw4j.common.util.StringUtils;

/**
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

    public DataRouteException(String message, Object... args) {
        super(StringUtils.replacePlaceholder(message, "{}", args));
    }

    public DataRouteException(Throwable cause, String message, Object... args) {
        super(StringUtils.replacePlaceholder(message, "{}", args), cause);
    }
}

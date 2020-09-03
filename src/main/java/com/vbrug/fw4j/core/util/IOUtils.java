package com.vbrug.fw4j.core.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author LK
 * @since 1.0
 */
public abstract class IOUtils {

    /**
     * @param closeables
     */
    public static void close(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
    }

}

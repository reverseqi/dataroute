package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.vbrug.fw4j.common.util.StringUtils;

/**
 * 执行器类别
 * @author vbrug
 * @since 1.0.0
 */
public enum ExecutorType {

    START("STAT"),
    EXTRACT("EXTRACT"),
    BSQL("BSQL"),
    EXPORT("EXPORT"),
    FILE_UD("FILE_UD"),
    HTTP("HTTP"),
    IMPORT("IMPORT");

    public final String value;

    private ExecutorType(String value) {
        this.value = value;
    }

    public static ExecutorType getByValue(String jdbcDriver) {
        for (ExecutorType value : values()) {
            if (value.value.equalsIgnoreCase(jdbcDriver))
                return value;
        }
        throw new DataRouteException(StringUtils.replacePlaceholder("{} 的枚举类不存在", jdbcDriver));
    }
}

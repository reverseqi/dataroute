package com.mingyi.dataroute.executor;

/**
 * 执行器类别
 * @author vbrug
 * @since 1.0.0
 */
public enum TaskType {

    START("STAT"),
    EXTRACT("EXTRACT"),
    BSQL("BSQL"),
    EXPORT("EXPORT"),
    FILE_UD("FILE_UD"),
    HTTP("HTTP"),
    IMPORT("IMPORT");

    public final String value;

    private TaskType(String value) {
        this.value = value;
    }

    public static TaskType getByValue(String jdbcDriver) {
        for (TaskType value : values()) {
            if (value.value.equalsIgnoreCase(jdbcDriver))
                return value;
        }
        return null;
    }
}

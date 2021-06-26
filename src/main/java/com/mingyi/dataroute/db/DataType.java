package com.mingyi.dataroute.db;

/**
 * @author vbrug
 * @since 1.0.0
 */
public enum DataType {

    DATETIME("DATETIME"),
    NUMBER("NUMBER"),
    STRING("STRING"),
    SQLSERVER("");

    public final String value;

    private DataType(String value) {
        this.value = value;
    }

    public static DataType getByValue(String dataType) {
        for (DataType value : values()) {
            if (value.value.equals(dataType))
                return value;
        }
        return null;
    }
}

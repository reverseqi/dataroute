package com.mingyi.dataroute.db;

/**
 * 执行语句类型
 * @author vbrug
 * @since 1.0.0
 */
public enum StatementType {

    DELETE("DELETE"), INSERT("INSERT"), SELECT("SELECT"), UPDATE("UPDATE");
    public final String value;

    StatementType(String value) {this.value = value;}

    public static StatementType getByValue(String statementType) {
        for (StatementType value : values()) {
            if (value.value.equalsIgnoreCase(statementType))
                return value;
        }
        return null;
    }

}

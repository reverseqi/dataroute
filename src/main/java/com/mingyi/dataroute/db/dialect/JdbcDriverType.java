package com.mingyi.dataroute.db.dialect;

/**
 * Jdbc驱动枚举类
 *
 * @author vbrug
 * @since 1.0.0
 */
public enum JdbcDriverType {

    MYSQL("com.mysql.jdbc.Driver"),
    ORACLE("oracle.jdbc.driver.OracleDriver"),
    CLICKHOUSE("ru.yandex.clickhouse.ClickHouseDriver"),
    SQLSERVER("");

    public final String value;

    private JdbcDriverType(String value){
        this.value = value;
    }

    public static JdbcDriverType getByValue(String jdbcDriver) {
        for (JdbcDriverType value : values()) {
            if (value.value.equals(jdbcDriver))
                return value;
        }
        return null;
    }
}

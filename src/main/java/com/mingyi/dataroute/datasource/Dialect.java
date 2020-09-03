package com.mingyi.dataroute.datasource;

/**
 * 数据库方言
 *
 * @author vbrug
 * @since 1.0.0
 */
public enum Dialect {

    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver"),
    MYSQL("mysql", "com.mysql.jdbc.Driver"),
    CLICKHOUSE("clickhouse", "ru.yandex.clickhouse.ClickHouseDriver");

    private String dbType;      // 数据库类型
    private String jdbcDriver;  // 数据库驱动

    private Dialect(String dbType, String jdbcDriver) {
        this.dbType = dbType;
        this.jdbcDriver = jdbcDriver;
    }

    public String dbType() {
        return this.dbType;
    }

    public String jdbcDriver() {
        return this.jdbcDriver;
    }

    public static Dialect getDBDialect(String dbType) {
        if (ORACLE.dbType.equals(dbType))
            return ORACLE;
        if (MYSQL.dbType.equals(dbType))
            return MYSQL;
        if (CLICKHOUSE.dbType.equals(dbType))
            return CLICKHOUSE;
        return null;
    }

    public String formatTriggerCond() {
        String triggerCond = null;
        switch (this) {
            case ORACLE:
                triggerCond = "to_date(?, 'yyyy-mm-dd HH24:MI:SS')";
                break;
            case MYSQL:
                triggerCond = "222";
                break;
            case CLICKHOUSE:
                triggerCond = "222d";
                break;
            default:
                triggerCond = "3333";
        }
        return triggerCond;

    }


    public String getJdbcUrl(String host, Integer port, String dbName){
        String jdbcUrl = null;
        switch (this) {
            case ORACLE:
                jdbcUrl = "jdbc:oracle:thin:@//"+host+":"+port+"/"+dbName;
                break;
            case MYSQL:
                jdbcUrl = "jdbc:mysql://"+host+":"+port+"/"+dbName;
                break;
            case CLICKHOUSE:
                jdbcUrl = "jdbc:clickhouse://"+host+":"+port+"/"+dbName;
                break;
            default:
                jdbcUrl = "3333";
        }
        return jdbcUrl;
    }
}

package com.mingyi.dataroute.db.dialect;


import java.util.Objects;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class DialectFactory {

    /**
     * 创建数据库
     *
     * @param driver 驱动名称
     * @return 数据库方言
     */
    public static Dialect createDSDialect(String driver) {
        switch (Objects.requireNonNull(JdbcDriverType.getByValue(driver))) {
            case MYSQL:
                return new MysqlDialect();
            case ORACLE:
                return new OracleDialect();
            case CLICKHOUSE:
                return new ClickHouseDialect();
            case SQLSERVER:
            default:
                return null;
        }
    }
}

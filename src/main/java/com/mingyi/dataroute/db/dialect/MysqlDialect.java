package com.mingyi.dataroute.db.dialect;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class MysqlDialect extends AbstractDialect {

    private static final String MYSQL_DATE_FORMAT = "%Y-%m-%d %H:%i:%s";

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.MYSQL;
    }

    @Override
    public String funcStringToDate(String vf) {
        return "STR_TO_DATE('" + vf + "', '" + MYSQL_DATE_FORMAT + "')";
    }

    @Override
    public String funcDateToString(String vf) {
        return "DATE_FORMAT(" + vf + ", '" + MYSQL_DATE_FORMAT + "')";
    }
}

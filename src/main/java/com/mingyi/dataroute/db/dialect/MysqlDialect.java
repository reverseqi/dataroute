package com.mingyi.dataroute.db.dialect;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class MysqlDialect implements Dialect {

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.MYSQL;
    }

    @Override
    public String vfString2Date(String vf) {
        return "STR_TO_DATE('" + vf + "', '%Y-%m-%d %H:%i:%s')";
    }

    @Override
    public String vfDate2String(String vf) {
        return "DATE_FORMAT(" + vf + ", '%Y-%m-%d %H:%i:%s')";
    }
}

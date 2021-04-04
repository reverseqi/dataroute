package com.mingyi.dataroute.db.dialect;

import com.vbrug.fw4j.common.util.Assert;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ClickHouse数据库方言
 *
 * @author vbrug
 * @since 1.0.0
 */
public class ClickHouseDialect implements Dialect {

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.CLICKHOUSE;
    }

    @Override
    public String vfString2Date(String vf) {
        return "toDateTime('" + vf + "')";
    }

    @Override
    public String vfDate2String(String vf) {
        return "toString(" + vf + ")";
    }

    @Override
    public String buildDeleteSQL(String table, String... conditions) {
        Assert.notNull(conditions, "conditions can not be null !");
        return new StringBuffer()
                .append(" ALTER TABLE ")
                .append(table)
                .append(" DELETE WHERE ")
                .append(Arrays.stream(conditions).map(x -> " (" + x + ") ").collect(Collectors.joining(" AND ")))
                .toString();
    }

}

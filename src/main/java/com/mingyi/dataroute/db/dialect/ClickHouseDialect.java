package com.mingyi.dataroute.db.dialect;

import com.vbrug.fw4j.common.util.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * ClickHouse数据库方言
 *
 * @author vbrug
 * @since 1.0.0
 */
public class ClickHouseDialect implements Dialect {

    @Override
    public String getExtractSql(String tableName, String fieldNames, String triggerDateField, String keyField, String defaultCond, Integer bufferSize) {
        String pageSql = new SQL() {{
            SELECT(fieldNames);
            FROM(tableName);
            WHERE((StringUtils.isEmpty(defaultCond) ? "" : (defaultCond + " and ")) + triggerDateField + " >= toDateTime(?)  and " + triggerDateField + " < toDateTime(?) ");
            ORDER_BY(triggerDateField + (StringUtils.isEmpty(keyField) ? "" : (", " + keyField)));
        }}.toString();
        return pageSql + " LIMIT " + bufferSize;
    }

    @Override
    public String getExtractSql(String tableName, String fieldNames, String triggerDateField, String defaultCond) {
        return new SQL() {{
            SELECT(fieldNames);
            FROM(tableName);
            WHERE((StringUtils.isEmpty(defaultCond) ? "" : (defaultCond + " and ")) + triggerDateField + " >= toDateTime(?) ");
        }}.toString();
    }

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.CLICKHOUSE;
    }
}

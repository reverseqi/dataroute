package com.mingyi.dataroute.db.dialect;

import com.vbrug.fw4j.common.util.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * Oracle方言
 *
 * @author vbrug
 * @since 1.0.0
 */
public class OracleDialect implements Dialect {

    @Override
    public String getExtractSql(String tableName, String fieldNames, String triggerDateField, String keyField, String defaultCond, Integer bufferSize) {
        String pageSql = new SQL() {{
            SELECT(fieldNames);
            FROM(tableName);
            WHERE((StringUtils.isEmpty(defaultCond) ? "" : (defaultCond + " and "))
                    + triggerDateField + " >= to_date(?, 'YYYY-MM-DD HH24:MI:SS') and "
                    + triggerDateField + " < to_date(?, 'YYYY-MM-DD HH24:MI:SS') ");
            ORDER_BY(triggerDateField + (StringUtils.isEmpty(keyField) ? "" : (", " + keyField)));
        }}.toString();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        sb.append(fieldNames);
        sb.append(" FROM ( ");
        sb.append(pageSql);
        sb.append(") WHERE ROWNUM <= ");
        sb.append(bufferSize);
        pageSql = sb.toString();
        return pageSql;
    }

    @Override
    public String getExtractSql(String tableName, String fieldNames, String triggerDateField, String defaultCond) {
        return new SQL() {{
            SELECT(fieldNames);
            FROM(tableName);
            WHERE((StringUtils.isEmpty(defaultCond) ? "" : (defaultCond + " and "))
                    + triggerDateField + " >= to_date(?, 'YYYY-MM-DD HH24:MI:SS') ");
        }}.toString();
    }

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.ORACLE;
    }
}

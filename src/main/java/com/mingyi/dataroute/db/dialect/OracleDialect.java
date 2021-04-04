package com.mingyi.dataroute.db.dialect;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.DateUtils;
import com.vbrug.fw4j.common.util.IOUtils;
import oracle.sql.TIMESTAMP;

import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Oracle方言
 *
 * @author vbrug
 * @since 1.0.0
 */
public class OracleDialect implements Dialect {

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.ORACLE;
    }

    @Override
    public String vfString2Date(String vf) {
        return "TO_DATE('" + vf + "', 'YYYY-MM-DD HH24:MI:SS')";
    }

    @Override
    public String vfDate2String(String vf) {
        return "TO_CHAR(" + vf + ", 'YYYY-MM-DD HH24:MI:SS')";
    }

    @Override
    public String buildQueryPageSQL(String table, String orderColumn, String[] columns, int page, int size, String... conditions) {
        return null;
    }

    @Override
    public String buildQueryTopSQL(String table, String orderColumn, String[] columns, int size, String... conditions) {
        return buildQuerySQL(table, columns, conditions) + " AND ROWNUM <= " + size;
    }

    @Override
    public Map<String, String> jdbcType2String(Map<String, Object> jdbcResultMap) {
        Iterator<Map.Entry<String, Object>> iterator = jdbcResultMap.entrySet().iterator();
        if (CollectionUtils.isEmpty(jdbcResultMap))
            return null;
        Map<String, String> resultMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String value = null;
            if (entry.getValue() instanceof Clob) {
                Clob clob = (Clob) entry.getValue();
                try {
                    value = IOUtils.getContent(clob.getCharacterStream());
                } catch (SQLException e) {
                    throw new DataRouteException(e);
                }
            } else if (entry.getValue() instanceof Blob) {
                Blob blob = (Blob) entry.getValue();
                try {
                    value = IOUtils.getContent(new InputStreamReader(blob.getBinaryStream()));
                } catch (SQLException e) {
                    throw new DataRouteException(e);
                }
            } else if (entry.getValue() instanceof Date) {
                Date timestamp = (Date) entry.getValue();
                value = DateUtils.formatTime(timestamp.getTime(), DateUtils.YMDHMS);
            } else if (entry.getValue() instanceof TIMESTAMP) {
                TIMESTAMP timestamp = (TIMESTAMP) entry.getValue();
                try {
                    value = DateUtils.formatTime(timestamp.dateValue().getTime(), DateUtils.YMDHMS);
                } catch (SQLException e) {
                    throw new RuntimeException("日期数据类型转换错误", e);
                }
            } else if (entry.getValue() != null) {
                value = String.valueOf(entry.getValue());
            }
            resultMap.put(entry.getKey(), value);
        }
        return resultMap;
    }

}

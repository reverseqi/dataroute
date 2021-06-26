package com.mingyi.dataroute.db.dialect;

import com.mingyi.dataroute.db.Field;
import com.mingyi.dataroute.exceptions.DataRouteException;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.DateUtils;
import com.vbrug.fw4j.common.util.IOUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import oracle.sql.TIMESTAMP;

import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;

/**
 * Oracle方言
 * @author vbrug
 * @since 1.0.0
 */
public class OracleDialect extends AbstractDialect {

    // 日期格式
    private static final String ORACLE_DATE_FORMAT = "YYYY-MM-DD HH24:MI:SS";

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.ORACLE;
    }

    @Override
    protected void fieldHandle(Field field, String value, StringBuilder sb, List<Object> argList) {
        if (StringUtils.isEmpty(value)) {
            if (field.isNullable())
                sb.append(NULL);
            else
                sb.append("''");
        } else {
            switch (field.getDataType()) {
                case DATETIME:
                    sb.append(this.funcStringToDate(value));
                    break;
                case NUMBER:
                    sb.append(value);
                    break;
                case STRING:
                default:
                    sb.append("'").append(value.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\")).append("'");
            }
        }
        sb.append(",");
    }

    @Override
    public String funcStringToDate(String vf) {
        return "TO_DATE('" + vf + "', '" + ORACLE_DATE_FORMAT + "')";
    }

    @Override
    public String funcDateToString(String vf) {
        return "TO_CHAR(" + vf + ", '" + ORACLE_DATE_FORMAT + "')";
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
            String                    value = null;
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
                    throw new DataRouteException(StringUtils.replacePlaceholder("Oracle Date: {}, format style {}, occur exception", timestamp, DateUtils.YMDHMS), e);
                }
            } else if (entry.getValue() != null) {
                value = String.valueOf(entry.getValue());
            }
            resultMap.put(entry.getKey(), value);
        }
        return resultMap;
    }

}

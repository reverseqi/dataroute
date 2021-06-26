package com.mingyi.dataroute.db.dialect;


import com.mingyi.dataroute.db.Field;
import com.mingyi.dataroute.db.SQL;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.DateUtils;
import com.vbrug.fw4j.common.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vbrug
 * @since 1.0.0
 */
public abstract class AbstractDialect implements Dialect {

    protected static final String NULL = "NULL";

    @Override
    public String buildInsertSQL(String tableName, List<Field> fieldList, List<Map<String, String>> dataList) {
        StringBuilder sb        = new StringBuilder();
        String        insertSQL = this.buildInsertSQL(tableName, fieldList.stream().map(Field::getFieldName).toArray(String[]::new));
        sb.append(insertSQL);

        return sb.toString();
    }

    protected void fieldHandle(Field field, String value, StringBuilder sb) {
        this.fieldHandle(field, value, sb, null);
    }

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
                    argList.add(value);
                    sb.append("?");
                    sb.append("'").append(value.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\")).append("'");
            }
        }
        sb.append(",");
    }

    /**
     * 构建查询SQL
     */
    public String buildQuerySQL(String table, String[] columns, String... conditions) {
        return buildQuerySQL(table, null, columns, conditions);
    }

    /**
     * 构建查询SQL
     */
    public String buildQuerySQL(String table, String orderColumn, String[] columns, String... conditions) {
        return new SQL() {{
            SELECT(columns);
            FROM(table);
            WHERE(conditions);
            ORDER_BY(orderColumn);
        }}.toString();
    }

    /**
     * 构建分页SQL
     */
    public String buildQueryPageSQL(String table, String orderColumn, String[] columns, int page, int size, String... conditions) {
        return buildQuerySQL(table, orderColumn, columns, conditions) + " LIMIT " + page + ", " + size;
    }

    /**
     * 构建Top查询SQL
     */
    public String buildQueryTopSQL(String table, String orderColumn, String[] columns, int size, String... conditions) {
        return buildQuerySQL(table, orderColumn, columns, conditions) + " LIMIT " + size;
    }

    /**
     * 构建删除SQL
     */
    public String buildDeleteSQL(String table, String... conditions) {
        return new SQL() {{
            DELETE_FROM(table);
            WHERE(conditions);
        }}.toString();
    }

    /**
     * 构建截断SQL
     */
    public String buildTruncateSQL(String tableName) {
        return "truncate table " + tableName;
    }

    /**
     * 构建插入sql
     */
    public String buildInsertSQL(String table, String... columns) {
        return new SQL() {{
            INSERT_INTO(table);
            INTO_COLUMNS(columns);
        }}.toString();
    }

    /**
     * 最大值函数
     * @param fieldName 字段名
     * @param alias     别名
     * @return 结果
     */
    public String funcMax(String fieldName, String alias) {
        return "max(" + fieldName + ") as " + alias;
    }

    /**
     * 计数函数
     * @param alias 别名
     * @return 结果
     */
    public String funcCount1(String alias) {
        return "count(1) as " + alias;
    }

    /**
     * 将JdbcType集合转为String
     * @param jdbcResultList 数据源
     * @return List<Map < String, String>>
     */
    public List<Map<String, String>> jdbcType2String(List<Map<String, Object>> jdbcResultList) {
        return jdbcResultList.stream().map(this::jdbcType2String).collect(Collectors.toList());
    }

    /**
     * 将JdbcType集合转为String
     * @param jdbcResultMap 源集合
     * @return Map<String, String>
     */
    public Map<String, String> jdbcType2String(Map<String, Object> jdbcResultMap) {
        Iterator<Map.Entry<String, Object>> iterator = jdbcResultMap.entrySet().iterator();
        if (CollectionUtils.isEmpty(jdbcResultMap))
            return null;
        Map<String, String> resultMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String                    value = null;
            if (entry.getValue() == null) {

            } else if (entry.getValue() instanceof Date) {
                Date timestamp = (Date) entry.getValue();
                value = DateUtils.formatTime(timestamp.getTime(), DateUtils.YMDHMS);
            } else {
                value = String.valueOf(entry.getValue());
            }
            resultMap.put(entry.getKey(), value);
        }
        return resultMap;
    }
}

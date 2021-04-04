package com.mingyi.dataroute.db.dialect;

import com.mingyi.dataroute.db.SQL;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.DateUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库方言接口
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface Dialect {

    JdbcDriverType getDialectType();

    /**
     * 构建查询SQL
     */
    default String buildQuerySQL(String table, String[] columns, String... conditions) {
        return buildQuerySQL(table, null, columns, conditions);
    }

    /**
     * 构建查询SQL
     */
    default String buildQuerySQL(String table, String orderColumn, String[] columns, String... conditions) {
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
    default String buildQueryPageSQL(String table, String orderColumn, String[] columns, int page, int size, String... conditions) {
        return buildQuerySQL(table, orderColumn, columns, conditions) + " LIMIT " + page + ", " + size;
    }

    /**
     * 构建Top查询SQL
     */
    default String buildQueryTopSQL(String table, String orderColumn, String[] columns, int size, String... conditions) {
        return buildQuerySQL(table, orderColumn, columns, conditions) + " LIMIT " + size;
    }

    /**
     * 构建删除SQL
     */
    default String buildDeleteSQL(String table, String... conditions) {
        return new SQL() {{
            DELETE_FROM(table);
            WHERE(conditions);
        }}.toString();
    }

    /**
     * 构建截断SQL
     */
    default String buildTruncateSQL(String tableName) {
        return "truncate table " + tableName;
    }

    /**
     * 构建插入sql
     */
    default String buildInsertSQL(String table, String... columns) {
        return new SQL() {{
            INSERT_INTO(table);
            INTO_COLUMNS(columns);
        }}.toString();
    }

    /**
     * 将值或字段转为数据库日期类型
     */
    String vfString2Date(String vf);

    /**
     * 将值或字段数据库日期类型转为字符串
     */
    String vfDate2String(String vf);

    /**
     * 将JdbcType集合转为String
     *
     * @param jdbcResultList 数据源
     * @return List<Map < String, String>>
     */
    default List<Map<String, String>> jdbcType2String(List<Map<String, Object>> jdbcResultList) {
        return jdbcResultList.stream().map(this::jdbcType2String).collect(Collectors.toList());
    }

    /**
     * 将JdbcType集合转为String
     *
     * @param jdbcResultMap 源集合
     * @return Map<String, String>
     */
    default Map<String, String> jdbcType2String(Map<String, Object> jdbcResultMap) {
        Iterator<Map.Entry<String, Object>> iterator = jdbcResultMap.entrySet().iterator();
        if (CollectionUtils.isEmpty(jdbcResultMap))
            return null;
        Map<String, String> resultMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String value = null;
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

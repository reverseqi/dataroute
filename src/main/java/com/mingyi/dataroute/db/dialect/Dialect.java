package com.mingyi.dataroute.db.dialect;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.DateUtils;
import com.vbrug.fw4j.common.util.IOUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.io.InputStreamReader;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库方言接口
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface Dialect {

    /**
     * 获取抽取sql
     */
    String getExtractSql(String tableName, String fieldNames, String triggerDateField, String keyField, String defaultCond, Integer bufferSize);

    /**
     * 获取抽取sql
     */
    String getExtractSql(String tableName, String fieldNames, String triggerDateField, String defaultCond);

    /**
     * 获取表中日期字段最大值
     */
    default String getMaxTimeSql(String tableName, String timeField, String defaultCond){
        String selectSql = new SQL(){{
            SELECT("max("+timeField+") max_"+timeField, "min("+timeField+") min_"+timeField);
            FROM(tableName);
        }}.toString();
        if (StringUtils.hasText(defaultCond))
            selectSql += " where " + defaultCond;
        return selectSql;
    }


    /**
     * List集合转为String
     *
     * @param dataList 数据源
     * @return List<Map < String, String>>
     */
    default List<Map<String, String>> result2String(List<Map<String, Object>> dataList) {
        return dataList.stream().map(this::result2String).collect(Collectors.toList());
    }

    /**
     * 结果转为字符串
     *
     * @param sourceMap 源集合
     * @return Map<String, String>
     */
    default Map<String, String> result2String(Map<String, Object> sourceMap) {
        Iterator<Map.Entry<String, Object>> iterator = sourceMap.entrySet().iterator();
        if (CollectionUtils.isEmpty(sourceMap))
            return null;
        Map<String, String> resultMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String value = null;
            if (entry.getValue() == null) {

            } else if (entry.getValue() instanceof Clob) {
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
            } else if (entry.getValue() instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) entry.getValue();
                value = DateUtils.formatTime(timestamp.getTime(), DateUtils.YMDHMS);
            } else {
                value = String.valueOf(entry.getValue());
            }
            resultMap.put(entry.getKey(), value);
        }
        return resultMap;
    }


    /**
     * 获取插入sql
     */
    default String getInsertSql(String tableName, String fieldNames) {
        return new SQL() {{
            INSERT_INTO(tableName);
            INTO_COLUMNS(fieldNames);
        }}.toString();
    }

    JdbcDriverType getDialectType();
}

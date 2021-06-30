package com.mingyi.dataroute.db.dialect;

import com.mingyi.dataroute.db.Field;

import java.util.List;
import java.util.Map;

/**
 * 数据库方言接口
 * @author vbrug
 * @since 1.0.0
 */
public interface Dialect {

    /**
     * 获取方言类型
     * @return 方言枚举类结果
     */
    JdbcDriverType getDialectType();

    /**
     * 根据内容构建插入SQL
     * @param tableName 表名
     * @param fieldList 字段
     * @param dataList  数据
     * @param argList 参数集合，若不为空，则使用预编译
     * @return 插入SQL
     */
    String buildInsertSQL(String tableName, List<? extends Field> fieldList, List<Map<String, String>> dataList, List<Object> argList);

    /**
     * 构建查询SQL
     */
    String buildQuerySQL(String table, String[] columns, String... conditions);

    /**
     * 构建查询SQL
     */
    String buildQuerySQL(String table, String orderColumn, String[] columns, String... conditions);

    /**
     * 构建分页SQL
     */
    String buildQueryPageSQL(String table, String orderColumn, String[] columns, int page, int size, String... conditions);

    /**
     * 构建Top查询SQL
     */
    String buildQueryTopSQL(String table, String orderColumn, String[] columns, int size, String... conditions);

    /**
     * 构建删除SQL
     */
    String buildDeleteSQL(String table, String... conditions);

    /**
     * 构建截断SQL
     */
    String buildTruncateSQL(String tableName);

    /**
     * 构建插入sql
     */
    String buildInsertSQL(String table, String... columns);

    /**
     * 函数将值或字段转为数据库日期类型
     */
    String funcStringToDate(String vf);

    /**
     * 函数将值或字段数据库日期类型转为字符串
     */
    String funcDateToString(String vf);

    /**
     * 最大值函数
     * @param fieldName 字段名
     * @param alias     别名
     * @return 结果
     */
    String funcMax(String fieldName, String alias);

    /**
     * 计数函数
     * @param alias 别名
     * @return 结果
     */
    String funcCount1(String alias);

    /**
     * 将JdbcType集合转为String
     * @param jdbcResultList 数据源
     * @return List<Map < String, String>>
     */
    List<Map<String, String>> jdbcType2String(List<Map<String, Object>> jdbcResultList);

    /**
     * 将JdbcType集合转为String
     * @param jdbcResultMap 源集合
     * @return Map<String, String>
     */
    Map<String, String> jdbcType2String(Map<String, Object> jdbcResultMap);


}

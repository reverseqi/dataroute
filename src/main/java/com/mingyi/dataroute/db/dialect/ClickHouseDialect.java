package com.mingyi.dataroute.db.dialect;

import com.mingyi.dataroute.db.StatementType;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ClickHouse数据库方言
 * @author vbrug
 * @since 1.0.0
 */
public class ClickHouseDialect extends AbstractDialect {

    private static final String CLICKHOUSE_DATE_FORMAT = "";

    @Override
    public JdbcDriverType getDialectType() {
        return JdbcDriverType.CLICKHOUSE;
    }

    @Override
    public String funcStringToDate(String vf) {
        return "toDateTime('" + vf + "')";
    }

    @Override
    public String funcDateToString(String vf) {
        return "toString(" + vf + ")";
    }

    @Override
    public String buildDeleteSQL(String table, String... conditions) {
        Assert.notNull(conditions, "conditions can not be null !");
        return " ALTER TABLE " +
                table +
                " DELETE WHERE " +
                Arrays.stream(conditions).map(x -> " (" + x + ") ").collect(Collectors.joining(" AND "));
    }

    /**
     * 构建ClickHouse的update、delete判断语句
     * @param database    数据库名称
     * @param tableName   表名
     * @param operateType 操作类型
     * @return 结果sql
     */
    public String buildUDAssertFinishSQL(String database, String tableName, StatementType operateType) {
        return buildUDAssertFinishSQL(database, tableName, operateType, null, null);
    }

    /**
     * 构建ClickHouse的update、delete判断语句
     * @param database      数据库名称
     * @param tableName     表名
     * @param operateType   操作类型
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     * @return 结果sql
     */
    public String buildUDAssertFinishSQL(String database, String tableName, StatementType operateType, String startDateTime, String endDateTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("select")
                .append(" multiIf(notEmpty(latest_fail_reason), 9, is_done) state, command, latest_fail_reason ")
                .append(" from ")
                .append(" system.mutations ")
                .append(" where match(command, '").append(operateType.value).append("') ")
                .append(" and table = '").append(tableName).append("' ");
        if (StringUtils.hasText(startDateTime))
            sb.append(" and create_time >= toDateTime(").append(startDateTime).append(")");
        if (StringUtils.hasText(endDateTime))
            sb.append(" and create_time <= toDateTime(").append(endDateTime).append(")");
        if (StringUtils.hasText(database))
            sb.append(" and database = '").append(database).append("' ");
        sb.append(" order by create_time desc limit 1 ");
        return sb.toString();
    }

}

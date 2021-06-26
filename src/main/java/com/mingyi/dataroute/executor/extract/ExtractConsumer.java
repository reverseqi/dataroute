package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.db.dialect.JdbcDriverType;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.ObjectUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.producecs.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConsumer implements Consumer<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractConsumer.class);

    private final ExtractConfigure configure;
    private final Dialect          dialect;
    private final AtomicLong       counter;
    private final Long             extractAmount;

    public ExtractConsumer(ExtractConfigure configure, AtomicLong counter, Long extractAmount) {
        this.configure = configure;
        this.dialect = configure.getTargetDataSource().getDialect();
        this.counter = counter;
        this.extractAmount = extractAmount;
    }

    @Override
    public Consumer<List<Map<String, String>>>[] split(int number) throws Exception {
        Assert.isTrue(number > 0, "number 必须大于0");
        if (number == 1) {
            return new ExtractConsumer[]{this};
        }
        List<ExtractConsumer> splitConsumers = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            splitConsumers.add(new ExtractConsumer(this.configure, this.counter, this.extractAmount));
        }
        return splitConsumers.toArray(new ExtractConsumer[0]);
    }

    @Override
    public void consume(List<Map<String, String>> dataList) throws SQLException {
        switch (dialect.getDialectType()) {
            case CLICKHOUSE:
                this.clickhouseHandle(dataList);
                break;
            case ORACLE:
            case MYSQL:
            default:
                this.defaultHandle(dataList);
        }
    }

    public void clickhouseHandle(List<Map<String, String>> dataList) throws SQLException {
        StringBuilder sb   = new StringBuilder();
        List<Object>  argList = new ArrayList<>();

        // 导入
        for (Map<String, String> map : dataList) {
            sb.append(" (");
            // a、处理抽取字段
            for (ExtractField extractField : configure.getExtractFieldList()) {
                String value = map.get(extractField.getTargetFieldName().toUpperCase().trim());
                this.fieldHandle(extractField, value, sb, argList);
            }
            // b、处理批次字段
            for (ExtractField extractField : configure.getExtractBatchFieldList()) {
                this.fieldHandle(extractField, extractField.getValue(), sb, argList);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") ,");
        }
        String insertSql = configure.buildInsertTargetSQL() + (" VALUES " + sb.substring(0, sb.length() - 2));
        this.executeInsert(insertSql, argList.toArray());
    }



    /**
     * oracle导入处理
     */
    public void defaultHandle(List<Map<String, String>> dataList) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> map : dataList) {
            // 不同数据库类型处理
            if (dialect.getDialectType() == JdbcDriverType.ORACLE)
                sb.append(" SELECT ");
            else
                sb.append("(");

            // a、处理抽取字段
            for (ExtractField extractField : configure.getExtractFieldList()) {
                String value = map.get(extractField.getTargetFieldName().toUpperCase().trim());
                this.fieldHandle2(extractField, value, sb, null);
            }

            // b、处理批次字段
            for (ExtractField extractField : configure.getExtractBatchFieldList()) {
                this.fieldHandle2(extractField, extractField.getValue(), sb, null);
            }
            sb.deleteCharAt(sb.length() - 1);

            // 不同数据库类型处理
            if (dialect.getDialectType() == JdbcDriverType.ORACLE)
                sb.append(" FROM DUAL UNION ALL ");
            else
                sb.append(") ,");
        }
        String insertSql = "";
        // 不同数据库类型处理
        if (dialect.getDialectType() == JdbcDriverType.ORACLE)
            insertSql = configure.buildInsertTargetSQL() + sb.substring(0, sb.length() - 10);
        else
            insertSql = configure.buildInsertTargetSQL() + " VALUES " + sb.substring(0, sb.length() - 2);
        this.executeInsert(insertSql);
    }


    private void fieldHandle2(ExtractField extractField, String value, StringBuilder sb, List<Object> argList) {
        if (StringUtils.isEmpty(value)) {
            if (extractField.isNullable())
                sb.append(NULL);
            else
                sb.append("''");
        } else {
            switch (extractField.getType().toUpperCase()) {
                case ExtractConfigure.FIELD_TYPE_DATETIME:
                    sb.append(dialect.funcStringToDate(value));
                    break;
                case ExtractConfigure.FIELD_TYPE_NUMBER:
                case ExtractConfigure.FIELD_TYPE_CONSTANT:
                    sb.append(value);
                    break;
                case ExtractConfigure.FIELD_TYPE_STRING:
                default:
                    sb.append("'").append(value.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\")).append("'");
            }
        }
        sb.append(",");
    }



    /**
     * 执行插入
     * @param insertSql 插入SQL
     * @param args      参数
     * @throws SQLException 异常信息
     */
    private void executeInsert(String insertSql, Object... args) throws SQLException {
        try {
            if (ObjectUtils.isNull(args))
                configure.getTargetDataSource().getSqlRunner().insert(insertSql);
            else
                configure.getTargetDataSource().getSqlRunner().insert(insertSql, args);
        } catch (SQLException e) {
            logger.error("【{}--{}】, 入库异常, 异常信息: {}, 异常SQL-> {} ",
                    configure.getTaskContext().getId(), configure.getTaskContext().getNodeName(),
                    e.getMessage(),
                    insertSql);
            throw e;
        }
    }
}

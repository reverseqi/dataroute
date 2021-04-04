package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.db.dialect.JdbcDriverType;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.pc.ConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConsumerHandler extends ConsumerHandler<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractConsumerHandler.class);

    private final ExtractDO ado;

    private final Dialect dialect;

    public ExtractConsumerHandler(ExtractDO ado) {
        this.ado = ado;
        this.dialect = ado.getTargetDataSource().getDialect();
    }

    @Override
    public void consume(List<Map<String, String>> dataList) throws SQLException {
        if (dialect.getDialectType() == JdbcDriverType.ORACLE)
            this.oracleHandle(dataList);
        else
            this.oracleHandle(dataList);
    }

    public void defaultHandle(List<Map<String, String>> dataList) throws SQLException {
        StringBuffer sqlSb = new StringBuffer();
        List<Object> argList = new ArrayList<>();

        // 导入
        for (Map<String, String> map : dataList) {
            sqlSb.append(" (");
            for (ExtractDO.Field field : ado.getFieldList()) {
                if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_CONSTANT)) {
                    sqlSb.append(field.getValue());
                } else {
                    String value = map.get(field.getName().toUpperCase().trim());
                    if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_DATETIME)) {
                        sqlSb.append(dialect.vfString2Date(value));
                    } else if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_NUMBER)) {
                        if (StringUtils.hasText(value)) {
                            sqlSb.append(value);
                        } else {
                            sqlSb.append("NULL");
                        }
                    } else {
                        if (StringUtils.hasText(value)) {
                            argList.add(value);
                            sqlSb.append("?");
                        } else {
                            sqlSb.append("''");
                        }
                    }
                }
                sqlSb.append(",");
            }
            sqlSb.deleteCharAt(sqlSb.length() - 1);
            sqlSb.append(") ,");
        }
        String insertSql = ado.getBatchInsertSql() + (" VALUES " + sqlSb.substring(0, sqlSb.length() - 2));
        try {
            ado.getTargetSqlRunner().insert(insertSql, argList.toArray());
            logger.info("【{}--{}】, 已入库：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), ado.getProduceCount().addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("【{}--{}】, 入库异常, 异常SQL-> {} ", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), insertSql);
            throw e;
        }
    }


    public void defaultHandle2(List<Map<String, String>> dataList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        List<Object> argList = new ArrayList<>();

        // 导入
        for (Map<String, String> map : dataList) {
            sb.append(", (");
            StringBuffer lineSb = new StringBuffer();
            for (ExtractDO.Field field : ado.getFieldList()) {
                if (field.getType() == ExecutorConstants.FIELD_CONSTANT) {
                    lineSb.append(", " + field.getValue());
                } else {
                    Object value = map.get(field.getName().toUpperCase().trim());
                    argList.add(value);
                    lineSb.append(", ?");
                }
            }
            sb.append(lineSb.substring(1));
            sb.append(")");
        }
        String insertSql = ado.getBatchInsertSql() + (" VALUES " + sb.substring(1));
        try {
            ado.getTargetSqlRunner().insert(insertSql, argList.toArray());
            logger.info("【{}--{}】, 已入库：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), ado.getProduceCount().addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("【{}--{}】, 入库异常：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), e);
            throw e;
        }
    }

    /**
     * oracle导入处理
     */
    public void oracleHandle(List<Map<String, String>> dataList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        for (Map<String, String> map : dataList) {
            // 不同数据库类型处理
            if (dialect.getDialectType() == JdbcDriverType.ORACLE)
                sb.append(" SELECT ");
            else
                sb.append("(");

            // 处理字段
            for (ExtractDO.Field field : ado.getFieldList()) {
                if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_CONSTANT)) {
                    if (StringUtils.isEmpty(field.getValue()))
                        sb.append("NULL");
                    else
                        sb.append(field.getValue());
                } else {
                    String value = map.get(field.getName().toUpperCase().trim());
                    if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_DATETIME)) {
                        if (StringUtils.isEmpty(value))
                            sb.append("NULL");
                        else
                            sb.append(dialect.vfString2Date(value));
                    } else if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_NUMBER)) {
                        if (StringUtils.hasText(value)) {
                            sb.append(value);
                        } else {
                            sb.append("NULL");
                        }
                    } else {
                        if (StringUtils.hasText(value))
                            sb.append("'").append(value.replaceAll("'", "''").replaceAll("\\\\", "\\\\\\\\")).append("'");
                        else
                            sb.append("''");
                    }
                }
                sb.append(",");
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
            insertSql = ado.getBatchInsertSql() + sb.substring(0, sb.length() - 10);
        else
            insertSql = ado.getBatchInsertSql() + " VALUES " + sb.substring(0, sb.length() - 2);
        try {
            ado.getTargetSqlRunner().run(insertSql);
            logger.info("【{}--{}】, 已入库：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(),
                    ado.getConsumeCount().addAndGet(dataList.size()));
        } catch (Exception e) {
            logger.error("【{}--{}】, 入库异常, 异常信息: {} ", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), e);
            logger.error("【{}--{}】, 入库异常, 异常SQL-> {} ", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), insertSql);
            throw e;
        }
    }

}

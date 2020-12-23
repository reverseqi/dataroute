package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.dialect.JdbcDriverType;
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

    private final ExtractConsumerDO consumerDO;

    private final JobSqlRunner sqlRunner;

    public ExtractConsumerHandler(ExtractConsumerDO consumerDO, JobSqlRunner sqlRunner) {
        this.consumerDO = consumerDO;
        this.sqlRunner = sqlRunner;
    }

    @Override
    public void consume(List<Map<String, String>> dataList) throws SQLException {
        if (consumerDO.getDataSource().getDialect().getDialectType() == JdbcDriverType.ORACLE)
            this.oracleHandle(dataList);
        else
            this.defaultHandle(dataList);
    }

    public void oracleHandle(List<Map<String, String>> dataList) throws SQLException {
        List<String> sqlList = new ArrayList<>();

        // 导入
        StringBuffer sb = new StringBuffer();
        for (Map<String, String> map : dataList) {
            sb.setLength(0);
            sb.append(" (");
            for (String fieldName : consumerDO.getFields().split(",")) {
                String value = map.get(fieldName.toUpperCase().trim());
                if (StringUtils.isEmpty(value))
                    value = "";
                sb.append("'").append(value).append("',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
            String insertSql = consumerDO.getBatchInsertSql() + (" VALUES " + sb.substring(1));
            sqlList.add(insertSql);
        }
        try {
            this.sqlRunner.runBatch(sqlList.toArray(new String[sqlList.size()]));
            logger.info("此次导入数据：{}, 总计导入数据量：{}", dataList.size(), consumerDO.getCount().addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("数据消费发生异常, 错误原因", e);
            throw e;
        }
    }

    public void defaultHandle(List<Map<String, String>> dataList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        List<String> argList = new ArrayList<>();

        // 导入
        for (Map<String, String> map : dataList) {
            sb.append(", (");
            StringBuffer lineSb = new StringBuffer();
            for (String fieldName : consumerDO.getFields().split(",")) {
                String value = map.get(fieldName.toUpperCase().trim());

                if (StringUtils.isEmpty(value))
                    value = "";
                argList.add(value);
                lineSb.append(", ?");
            }
            sb.append(lineSb.substring(1));
            sb.append(")");
        }
        String insertSql = consumerDO.getBatchInsertSql() + (" VALUES " + sb.substring(1));
        try {
            this.sqlRunner.insert(insertSql, argList.toArray());
            logger.info("此次导入数据：{}, 总计导入数据量：{}", dataList.size(), consumerDO.getCount().addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("数据消费发生异常, 错误原因{}", e.getMessage());
            throw e;
        }
    }
}

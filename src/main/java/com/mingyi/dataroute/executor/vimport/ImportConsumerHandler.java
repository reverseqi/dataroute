package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.db.dialect.JdbcDriverType;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.pc.ConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ImportConsumerHandler extends ConsumerHandler<List<Map<String, Object>>> {

    private static final Logger logger = LoggerFactory.getLogger(ImportConsumerHandler.class);

    private final AtomicInteger counter = new AtomicInteger(0);

    private final ImportConsumerDO consumerDO;

    private final TaskContext taskContext;

    public ImportConsumerHandler(ImportConsumerDO consumerDO, TaskContext taskContext) {
        this.consumerDO = consumerDO;
        this.taskContext = taskContext;
    }

    @Override
    public void consume(List<Map<String, Object>> dataList) throws SQLException {
        if (consumerDO.getDataSource().getDialect().getDialectType() == JdbcDriverType.ORACLE)
            this.oracleHandle(dataList);
        else
            this.defaultHandle(dataList);
    }

    public void oracleHandle(List<Map<String, Object>> dataList) throws SQLException {
        List<String> sqlList = new ArrayList<>();

        // 导入
        StringBuffer sb = new StringBuffer();
        for (Map<String, Object> map : dataList) {
            sb.setLength(0);
            sb.append(" (");
            for (String fieldName : consumerDO.getFields().split(",")) {
                String value = Optional.ofNullable(map.get(StringUtils.lineToHump(fieldName.trim()))).map(String::valueOf).orElse(null);
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
            consumerDO.getSqlRunner().runBatch(sqlList.toArray(new String[sqlList.size()]));
            logger.info("任务--> {}--{}，此次导入：{}, 总计导入：{}",
                    taskContext.getId(), taskContext.getNodeName(), dataList.size(), counter.addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("任务--> {}--{}，导入发生异常：{}", taskContext.getId(), taskContext.getNodeName(), e);
            throw e;
        }
    }

    public void defaultHandle(List<Map<String, Object>> dataList) throws SQLException {
        StringBuffer sb = new StringBuffer();
        List<String> argList = new ArrayList<>();

        // 导入
        for (Map<String, Object> map : dataList) {
            sb.append(", (");
            StringBuffer lineSb = new StringBuffer();
            for (String fieldName : consumerDO.getFields().split(",")) {
                String value = Optional.ofNullable(map.get(StringUtils.lineToHump(fieldName.trim()))).map(String::valueOf).orElse(null);
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
            consumerDO.getSqlRunner().insert(insertSql, argList.toArray());
            logger.info("任务--> {}--{}，此次导入：{}, 总计导入：{}",
                    taskContext.getId(), taskContext.getNodeName(), dataList.size(), counter.addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("任务--> {}--{}，导入发生异常：{}", taskContext.getId(), taskContext.getNodeName(), e);
            throw e;
        }
    }
}

package com.mingyi.dataroute.executor.export;

import com.vbrug.fw4j.common.util.NumberUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.producecs.ProducerTask;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导出生产任务
 * @author vbrug
 * @since 1.0.0
 */
public class ExportProducerTask implements ProducerTask<List<Map<String, String>>> {

    private static final Logger            logger = LoggerFactory.getLogger(ExportProducerTask.class);
    private final        ExportConfigure   configure;
    private final        TaskContext       taskContext;
    private final        String            exportSql;
    private              ResultSet         rs;
    private              ResultSetMetaData metaData;
    private final        AtomicLong        counter;

    public ExportProducerTask(ExportConfigure configure, TaskContext taskContext, AtomicLong counter) {
        this.exportSql = configure.getPo().getExportSql();
        this.configure = configure;
        this.taskContext = taskContext;
        this.counter = counter;
    }

    @Override
    public void beforeProduce() throws Exception {
        Connection        connection = configure.getDataSource().getConnection();
        PreparedStatement statement  = connection.prepareStatement(exportSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(configure.getBufferFetchSize());
        this.rs = statement.executeQuery();
        this.metaData = rs.getMetaData();
        logger.info("【{}--{}】，数据抽取SQL ->> {}", taskContext.getTaskId(), taskContext.getTaskName(), StringUtils.removeTNR(exportSql));
    }

    @Override
    public List<Map<String, String>> produce() throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(metaData.getColumnName(i).toUpperCase(), rs.getObject(i));
            }
            list.add(map);
            if (list.size() >= configure.getBufferFetchSize()) {
                logger.info("【{}--{}】, 已生产：{}，当前生产进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                        counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter, configure.getExportAmount()));
                return configure.getDataSource().getDialect().jdbcType2String(list);
            }
        }
        if (list.size() > 0) {
            logger.info("【{}--{}】, 已生产：{}，当前生产进度：{}", taskContext.getTaskId(), taskContext.getTaskName(),
                    counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter, configure.getExportAmount()));
            return configure.getDataSource().getDialect().jdbcType2String(list);
        } else
            return null;
    }

    @Override
    public void finallyHandle() throws Exception {
        try {
            if (Objects.nonNull(rs) && !rs.isClosed()) {
                this.rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info("【{}--{}】, ResultSet资源关闭, 生产者{} 导出结束", taskContext.getTaskId(), taskContext.getTaskName(), Thread.currentThread().getName());
    }
}

package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.db.Field;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.core.design.producecs.ConsumerTask;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导入消费者任务
 * @author vbrug
 * @since 1.0.0
 */
public class ImportConsumerTask implements ConsumerTask<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ImportConsumerTask.class);

    private final ImportConfigure configure;
    private       TaskContext     taskContext;
    private final Dialect         dialect;
    private final AtomicLong      counter;

    public ImportConsumerTask(ImportConfigure configure, TaskContext taskContext, AtomicLong counter) {
        this.configure = configure;
        this.dialect = configure.getDataSource().getDialect();
        this.counter = counter;
    }

    @Override
    public ConsumerTask<List<Map<String, String>>>[] split(int number) throws Exception {
        Assert.isTrue(number > 0, "number 必须大于0");
        if (number == 1) {
            return new ImportConsumerTask[]{this};
        }
        List<ImportConsumerTask> splitConsumers = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            splitConsumers.add(new ImportConsumerTask(this.configure, this.taskContext, this.counter));
        }
        return splitConsumers.toArray(new ImportConsumerTask[0]);
    }

    @Override
    public void consume(List<Map<String, String>> dataList) throws SQLException {
        List<Object> argList   = new ArrayList<>();
        List<Field>  fieldList = new ArrayList<>();
        fieldList.addAll(configure.getImportFieldList());
        fieldList.addAll(configure.getImportBatchFieldList());
        String insertSQL = dialect.buildInsertSQL(configure.getPo().getImportTableName(), fieldList, dataList, argList);
        try {
            if (CollectionUtils.isEmpty(argList))
                configure.getDataSource().getSqlRunner().insert(insertSQL);
            else
                configure.getDataSource().getSqlRunner().insert(insertSQL, argList);

            logger.info("【{}--{}】, 已入库：{} ", taskContext.getTaskId(), taskContext.getTaskName(), counter.addAndGet(dataList.size()));
        } catch (SQLException e) {
            logger.error("【{}--{}】, 入库异常, 异常信息: {}, 异常SQL-> {} ",
                    taskContext.getTaskId(), taskContext.getTaskName(),
                    e.getMessage(),
                    insertSQL);
            throw e;
        }
    }
}

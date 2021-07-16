package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.dialect.Dialect;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.NumberUtils;
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
 * 抽取
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConsumerTask implements ConsumerTask<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractConsumerTask.class);

    private final ExtractConfigure configure;
    private       TaskContext      taskContext;
    private final Dialect          dialect;
    private final AtomicLong       counter;
    private final Long             extractAmount;

    public ExtractConsumerTask(ExtractConfigure configure, TaskContext taskContext, AtomicLong counter, Long extractAmount) {
        this.configure = configure;
        this.dialect = configure.getTargetDataSource().getDialect();
        this.counter = counter;
        this.extractAmount = extractAmount;
    }

    @Override
    public ConsumerTask<List<Map<String, String>>>[] split(int number) throws Exception {
        Assert.isTrue(number > 0, "number 必须大于0");
        if (number == 1) {
            return new ExtractConsumerTask[]{this};
        }
        List<ExtractConsumerTask> splitConsumers = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            splitConsumers.add(new ExtractConsumerTask(this.configure, this.taskContext, this.counter, this.extractAmount));
        }
        return splitConsumers.toArray(new ExtractConsumerTask[0]);
    }

    @Override
    public void consume(List<Map<String, String>> dataList) throws SQLException {
        List<Object>       argList   = new ArrayList<>();
        List<ExtractField> fieldList = new ArrayList<>();
        fieldList.addAll(configure.getExtractFieldList());
        fieldList.addAll(configure.getExtractBatchFieldList());
        String insertSQL = dialect.buildInsertSQL(configure.getPo().getTargetTable(), fieldList, dataList, argList);
        try {
            if (CollectionUtils.isEmpty(argList))
                configure.getTargetDataSource().getSqlRunner().insert(insertSQL);
            else
                configure.getTargetDataSource().getSqlRunner().insert(insertSQL, argList);
            logger.info("【{}--{}】, 已入库：{}，当前入库进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                    counter.addAndGet(dataList.size()), NumberUtils.divisionPercent(counter, configure.getExtractAmount()));
        } catch (SQLException e) {
            logger.error("【{}--{}】, 入库异常, 异常信息: {}, 异常SQL-> {} ",
                    taskContext.getTaskId(), taskContext.getTaskName(),
                    e.getMessage(),
                    insertSQL);
            throw e;
        }
    }
}

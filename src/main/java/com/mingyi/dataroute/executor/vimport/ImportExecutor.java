package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.executor.*;
import com.mingyi.dataroute.persistence.node.vimport.po.ImportPO;
import com.mingyi.dataroute.persistence.node.vimport.service.ImportService;
import com.vbrug.fw4j.core.design.pc.Consumer;
import com.vbrug.fw4j.core.design.pc.PCHelper;
import com.vbrug.fw4j.core.design.pc.Producer;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.fw4j.core.thread.SignalLock;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ImportExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ImportExecutor.class);

    private final TaskContext taskContext;

    public ImportExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        // 01-查询任务实体
        ImportPO importPO = SpringHelp.getBean(ImportService.class).findById(taskContext.getNodeId());

        // 02-处理环境变量
        importPO.setFilePath(ParamParser.parseParam(importPO.getFilePath(), new ParamTokenHandler(taskContext)));

        // 03-判断导入处理类型
        if (importPO.getHandleType().equalsIgnoreCase(ExecutorConstants.SINK_DB_TYPE_TRUNCATE)) {
            this.truncateMdTable(importPO);
            logger.info("任务--> {}--{}，清空中间表 {}", taskContext.getTaskId(), taskContext.getTaskName(), importPO.getTableName());
        }

        // 04-执行数据导入
        this.importData(importPO);
        return null;
    }


    /**
     * 清空中间表
     */
    private void truncateMdTable(ImportPO importPO) throws SQLException {
        DataSourcePool.getInstance().getDataSource(importPO.getDatasourceId()).getSqlRunner().run("truncate table " + importPO.getTableName());
    }

    public void importData(ImportPO importPO) throws Exception {
        // 01-初始化锁和队列
        ConcurrentLinkedDeque<List<Map<String, Object>>> deque    = new ConcurrentLinkedDeque<>();
        SignalLock                                       lock     = new SignalLock(true);
        PCHelper                                         pcHelper = new PCHelper();

        // 02-处理生产者线程
        ImportProducerHandler producerHandler = new ImportProducerHandler(importPO.getFilePath(), importPO);
        pcHelper.addProducer(new Producer<>(producerHandler, deque, lock, true, true));

        // 03-处理消费者线程
        ImportConsumerDO      consumerDO      = new ImportConsumerDO(importPO, DataSourcePool.getInstance().getDataSource(importPO.getDatasourceId()));
        ImportConsumerHandler consumerHandler = new ImportConsumerHandler(consumerDO, taskContext);
        for (Integer i = 0; i < importPO.getConsumerNumber(); i++) {
            pcHelper.addConsumer(new Consumer<>(consumerHandler, deque, lock));
        }

        // 05-完成任务
        pcHelper.start();
        pcHelper.finishProducer();
        pcHelper.finishConsumer();
        producerHandler.close();

        // 判断是否执行成功
        if (!pcHelper.isSuccess()) {
            logger.error("任务--> {}--{}，生产消费异常，任务执行失败！", taskContext.getTaskId(), taskContext.getTaskName());
            throw new Exception("生产消费异常，任务执行失败");
        }
    }
}

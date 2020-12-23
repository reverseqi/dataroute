package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.DateUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.core.design.pc.Consumer;
import com.vbrug.fw4j.core.design.pc.PCHelper;
import com.vbrug.fw4j.core.design.pc.Producer;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.fw4j.core.thread.SignalLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 抽取执行器
 *
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExtractExecutor.class);

    private final TaskContext taskContext;
    private final JobContext jobContext;
    private ExtractPO extractPO;

    public ExtractExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.jobContext = taskContext.getJobContext();
    }

    @Override
    public void execute() throws Exception {
        ExtractService service = SpringHelp.getBean(ExtractService.class);
        extractPO = service.findById(jobContext.getProcessId(), taskContext.getNodeId());

        // 01-判断是否需要清空中间表
        if ("TRUNCATE".equals(extractPO.getHandleType())) {
            truncateMdTable(extractPO);
            logger.info("【{}--{}】，清空中间表 {}", taskContext.getId(), taskContext.getNodeName(), extractPO.getTargetTable());
        }

        // 03-数据消费生产
        this.syncData();
        logger.info("【{}--{}】，数据抽取完成", taskContext.getId(), taskContext.getNodeName());

        // 04-更新触发日期
        Map<String, Object> valueMap = CollectionUtils.createValueMap().add(extractPO.getTriggerField().toUpperCase(), this.getSyncMaxTime()).build();
        this.updateTriggerCond(service, valueMap);
        logger.info("【{}--{}】，数据同步完成。条件更新为: {}", taskContext.getId(), taskContext.getNodeName(), valueMap);
    }


    @SuppressWarnings("rawtypes")
    private void syncData() throws Exception {
        // 01-初始化锁和队列
        ConcurrentLinkedDeque<List<Map<String, String>>> deque = new ConcurrentLinkedDeque<>();
        SignalLock lock = new SignalLock(true);

        // 02-获取上次时间条件
        Map parseMap = JacksonUtils.json2Map(extractPO.getLastTriggerValue());
        String lastSyncTime = String.valueOf(parseMap.get(extractPO.getTriggerField().toUpperCase()));
        Assert.notNull(lastSyncTime, "抽取触发初始值为空");

        DataSourcePool dsPool = taskContext.getJobContext().getDsPool();
        List<Producer> producerList = new ArrayList<>();
        List<Consumer> consumerList = new ArrayList<>();

        // 03-处理生产者线程
        ExtractProducerDO producerDO = new ExtractProducerDO(extractPO, dsPool.getDataSource(extractPO.getOriginDatasource()));
        logger.info("【{}--{}】，抽取SQL-->{}", taskContext.getId(), taskContext.getNodeName(), producerDO.getExtractSql());
        ExtractProducerHandler extractProducerHandler = new ExtractProducerHandler(producerDO, lastSyncTime);
        Producer<List<Map<String, String>>> producer = new Producer<>(extractProducerHandler, deque, lock, true);
        producerList.add(producer);
        producer.start();
        logger.info("【{}--{}】，生产者启动{}", taskContext.getId(), taskContext.getNodeName(), producer);

        // 04-处理消费者线程
        ExtractConsumerDO consumerBean = new ExtractConsumerDO(extractPO, dsPool.getDataSource(extractPO.getTargetDatasource()));
        JobDataSource consumerDataSource = dsPool.getDataSource(extractPO.getTargetDatasource());
        ExtractConsumerHandler consumerHandler = new ExtractConsumerHandler(consumerBean, new JobSqlRunner(consumerDataSource.getConnection()));
        for (Integer i = 0; i < extractPO.getConsumerNumber(); i++) {
            Consumer<List<Map<String, String>>> consumer = new Consumer<>(consumerHandler, deque, lock);
            consumerList.add(consumer);
            consumer.start();
            logger.info("【{}--{}】，消费者启动{}", taskContext.getId(), taskContext.getNodeName(), consumer);
        }

        // 05-完成任务
        PCHelper pcHelper = new PCHelper(producerList, consumerList);
        pcHelper.finishProducer();
        extractProducerHandler.close();
        pcHelper.finishConsumer();
    }

    /**
     * 清空中间表
     */
    private void truncateMdTable(ExtractPO extractPO) throws SQLException {
        jobContext.getSqlRunner(extractPO.getTargetDatasource()).run("truncate table " + extractPO.getTargetTable());
    }

    /**
     * 更新触发条件
     */
    private void updateTriggerCond(ExtractService service, Map<String, Object> triggerMap) {
        service.updateTriggerValue(jobContext.getProcessId(), taskContext.getNodeId(), JacksonUtils.bean2Json(triggerMap));
    }

    /**
     * 查询原始库最大时间
     */
    private String getSyncMaxTime() throws SQLException {
        JobDataSource dataSource = jobContext.getDsPool().getDataSource(extractPO.getTargetDatasource());
        String maxTimeSql = dataSource.getDialect().getMaxTimeSql(extractPO.getTargetTable(), extractPO.getTriggerField(), null);
        logger.info("【{}--{}】，同步数据最新时间查询SQL-->{}", jobContext.getJobId(), taskContext.getNodeId(), maxTimeSql);
        Map<String, Object> resultMap = new JobSqlRunner(dataSource.getConnection()).selectOne(maxTimeSql);
        Map<String, String> ssMap = dataSource.getDialect().result2String(resultMap);
        String maxTime = ssMap.get(("max_" + extractPO.getTriggerField()).toUpperCase());
        return DateUtils.formatTime(DateUtils.parseDate(maxTime, DateUtils.YMDHMS).getTime() + 1000, DateUtils.YMDHMS);
    }

}

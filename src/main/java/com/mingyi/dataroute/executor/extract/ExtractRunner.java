package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.node.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.core.design.producecs.PCPool;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractRunner {
    public static final Logger logger = LoggerFactory.getLogger(ExtractRunner.class);

    private final TaskContext      taskContext;
    private final ExtractConfigure configure;

    public ExtractRunner(ExtractPO po, TaskContext taskContext) throws SQLException, IOException {
        this.taskContext = taskContext;
        this.configure = new ExtractConfigure(po, taskContext);
    }

    /**
     * 判断此次同步是否有新数据
     */
    public boolean hasNewData() throws SQLException {
        String              extractRangeSQL = configure.buildExtractRangeSQL();
        Map<String, Object> result          = configure.getOriginDataSource().getSqlRunner().selectOne(extractRangeSQL);
        if (CollectionUtils.isEmpty(result))
            return false;
        // 获取结果
        Map<String, String> resultMap = configure.getOriginDataSource().getDialect().jdbcType2String(result);
        configure.getCondField().setMaxValue(resultMap.get(ExtractConfigure.FIELD_MAX_VALUE));
        configure.setExtractAmount(Long.parseLong(resultMap.get(ExtractConfigure.FIELD_EXTRACT_AMOUNT)));
        // 判断结果
        if (configure.getExtractAmount() == 0)
            return false;
        logger.info("【{}--{}】，此次抽取范围为 {}～{}, 总计：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                configure.getCondField().getMinValue(), configure.getCondField().getMaxValue(),
                configure.getExtractAmount());
        return true;
    }

    /**
     * 清空中间表
     */
    public void truncateTargetTable() throws SQLException {
        String truncateTargetSQL = configure.buildTruncateTargetSQL();
        configure.getTargetDataSource().getSqlRunner().run(truncateTargetSQL);
        logger.info("【{}--{}】，清空目标表 --> {}", taskContext.getTaskId(), taskContext.getTaskName(), truncateTargetSQL);
    }

    /**
     * 数据抽取
     */
    public long extractData() throws Exception {

        // 消费者
        ExtractConsumer consumer = new ExtractConsumer(this.configure, new AtomicLong(0L), configure.getExtractAmount());

        // 生产者
        ExtractProducer producer = new ExtractProducer(configure.buildExtractSQL(),
                configure.getCondField().getMinValue(), configure.getCondField().getMaxValue(),
                configure, new AtomicLong(0L));

        // 启动生产消费者线程池
        new PCPool<List<Map<String, String>>>(String.valueOf(taskContext.getJobContext().getJobId())).setDequeMaxSize(configure.getDequeMaxSize())
                .push(consumer.split(configure.getPo().getConsumerNumber()))
                .push(producer.split(configure.getPo().getProducerNumber())).run();

        return configure.getExtractAmount();
    }

    /**
     * 更新触发条件
     */
    public void updateTrigger() throws SQLException {
        ExtractField condField = configure.getCondField();
        switch (condField.getProperty().toUpperCase()) {
            case ExtractConfigure.FIELD_PROPERTY_RANGE:
                return;
            case ExtractConfigure.FIELD_PROPERTY_MINIMUM_EXCLUDE:
            case ExtractConfigure.FIELD_PROPERTY_MINIMUM_INCLUDE:
            default:
                condField.setMinValue(condField.getMaxValue());
                condField.setMaxValue(null);
        }
        String condFieldJSON = JacksonUtils.bean2Json(condField);
        SpringHelp.getBean(ExtractService.class).updateTriggerValue(taskContext.getNodeId(), condFieldJSON);
        logger.info("【{}--{}】，更新同步条件{}。", taskContext.getTaskId(), taskContext.getTaskName(), condFieldJSON);
    }
}

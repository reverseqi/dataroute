package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.core.design.producecs.PCPool;
import com.vbrug.fw4j.core.spring.SpringHelp;
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
        Map<String, String> resultMap = configure.getOriginDataSource().getDialect().jdbcType2String(result);
        configure.getExtractCondField().setMaxValue(resultMap.get(ExtractConfigure.FIELD_MAX_VALUE));
        configure.setExtractAmount(Long.parseLong(resultMap.get(ExtractConfigure.FIELD_EXTRACT_AMOUNT)));
        if (configure.getExtractAmount() == 0)
            return false;
        logger.info("【{}--{}】，此次抽取范围为 {}～{}, 总计：{} ", taskContext.getId(), taskContext.getNodeName(),
                configure.getExtractCondField().getMinValue(), configure.getExtractCondField().getMaxValue(),
                configure.getExtractAmount());
        return true;
    }

    /**
     * 清空中间表
     */
    public void truncateTargetTable() throws SQLException {
        String truncateTargetSQL = configure.buildTruncateTargetSQL();
        configure.getTargetDataSource().getSqlRunner().run(truncateTargetSQL);
        logger.info("【{}--{}】，清空目标表 --> {}", taskContext.getId(), taskContext.getNodeName(), truncateTargetSQL);
    }

    /**
     * 数据抽取
     */
    public void extractData() throws Exception {
        new PCPool<List<Map<String, String>>>(String.valueOf(taskContext.getJobContext().getJobId())).setDequeMaxSize(5)
                .push(new ExtractConsumer(this.configure, new AtomicLong(0L), configure.getExtractAmount()).split(configure.getPo().getConsumerNumber()))
                .push(new ExtractProducer(configure.getExtractCondField().getMinValue(), configure.getExtractCondField().getMaxValue(), configure,
                        configure.getOriginDataSource().getConnection(), new AtomicLong(0L), configure.getExtractAmount())
                        .split(configure.getPo().getProducerNumber())).run();
    }

    /**
     * 更新触发条件
     */
    public void updateTrigger() throws SQLException {
        String condFieldJSON = JacksonUtils.bean2Json(configure.getExtractCondField());
        SpringHelp.getBean(ExtractService.class).updateTriggerValue(taskContext.getNodeId(), condFieldJSON);
        logger.info("【{}--{}】，更新同步条件{}。", taskContext.getId(), taskContext.getNodeName(), condFieldJSON);
    }
}

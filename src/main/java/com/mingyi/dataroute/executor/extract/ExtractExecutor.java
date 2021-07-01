package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.node.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽取执行器
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExtractExecutor.class);

    private final TaskContext taskContext;

    public ExtractExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        // 01-初始化变量
        ExtractService persistenceService = SpringHelp.getBean(ExtractService.class);
        ExtractPO      extractPO          = persistenceService.findById(taskContext.getNodeId());
        ExtractRunner  extractRunner      = new ExtractRunner(extractPO, taskContext);

        // 02-判断是否有新数据
        if (!extractRunner.hasNewData()) {
            logger.info("【{}--{}】，当前无新数据，数据抽取结束", taskContext.getTaskId(), taskContext.getTaskName());
            // 无数据，另行处理
            return TaskResult.newInstance(taskContext.getTaskId()).setPrecondition(TaskResult.PRECONDITION_TWO);
        }

        // 03-判断是否需要清空中间表
        if (extractPO.getSinkDbType().equalsIgnoreCase(ExecutorConstants.SINK_DB_TYPE_TRUNCATE)) {
            extractRunner.truncateTargetTable();
        }

        // 03-数据消费生产
        long extractAmount = extractRunner.extractData();

        // 05-更新触发日期
        extractRunner.updateTrigger();
        return TaskResult.newInstance(taskContext.getTaskId()).setPrecondition(TaskResult.PRECONDITION_YES)
                .setRemark(StringUtils.replacePlaceholder("此次共抽取{}", extractAmount));
    }

}

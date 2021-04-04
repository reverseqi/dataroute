package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    public ExtractExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.jobContext = taskContext.getJobContext();
    }

    @Override
    public void execute() throws Exception {
        // 01-初始化变量
        ExtractService persistenceService = SpringHelp.getBean(ExtractService.class);
        ExtractPO extractPO = persistenceService.findById(jobContext.getProcessId(), taskContext.getNodeId());
        ExtractRunner extractRunner = new ExtractRunner(extractPO, taskContext);

        // 02-判断是否有新数据
        if (!extractRunner.hasNewData()) {
            logger.info("【{}--{}】，当前无新数据，数据抽取结束", taskContext.getId(), taskContext.getNodeName());
            return;
        }

        // 03-判断是否需要清空中间表
        if (extractPO.getHandleType().equalsIgnoreCase(ExecutorConstants.TRUNCATE)) {
            extractRunner.truncateMdTable();
            logger.info("【{}--{}】，清空中间表 {}", taskContext.getId(), taskContext.getNodeName(), extractPO.getTargetTable());
        }

        // 03-数据消费生产
        extractRunner.extractData();
        logger.info("【{}--{}】，数据抽取完成", taskContext.getId(), taskContext.getNodeName());

        // 04-更新触发日期
        List<ExtractDO.Field> triggerList = extractRunner.updateTrigger();
        logger.info("【{}--{}】，更新同步条件{}。", taskContext.getId(), taskContext.getNodeName(), JacksonUtils.bean2Json(triggerList));
    }

}

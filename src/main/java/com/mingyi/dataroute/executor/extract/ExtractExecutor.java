package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.executor.ContextParamParser;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.node.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

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
        // 01-构建执行器
        ExtractRunner extractRunner = this.buildRunner();

        // 02-判断是否有新数据
        if (!extractRunner.hasNewData()) {
            logger.info("【{}--{}】，当前无新数据，数据抽取结束", taskContext.getTaskId(), taskContext.getTaskName());
            // 无数据，另行处理
            return TaskResult.newInstance(taskContext.getTaskId()).setPrecondition(TaskResult.PRECONDITION_TWO);
        }

        // 03-抽取数据
        long extractAmount = extractRunner.extractData();
        return TaskResult.newInstance(taskContext.getTaskId()).setPrecondition(TaskResult.PRECONDITION_YES)
                .setRemark(StringUtils.replacePlaceholder("此次共抽取{}", extractAmount));
    }

    /**
     * 构建执行器
     * @return 结果
     */
    private ExtractRunner buildRunner() throws SQLException, IOException {
        // 查询抽取对象实体
        ExtractPO extractPO = SpringHelp.getBean(ExtractService.class).findById(taskContext.getNodeId());

        // 处理环境变量
        extractPO.setOriginTable(ContextParamParser.parseParam(extractPO.getOriginTable(), taskContext));
        extractPO.setTargetTable(ContextParamParser.parseParam(extractPO.getTargetTable(), taskContext));
        extractPO.setExtractBatchFields(ContextParamParser.parseParam(extractPO.getExtractBatchFields(), taskContext));

        // 构建配置类
        return new ExtractRunner(new ExtractConfigure(extractPO), taskContext);
    }
}

package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.executor.ContextParamParser;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.persistence.node.vimport.po.ImportPO;
import com.mingyi.dataroute.persistence.node.vimport.service.ImportService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据导入工具
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
        TaskResult taskResult = TaskResult.newInstance(taskContext.getTaskId());
        this.buildRunner().parseFileAndSinkDB();
        return taskResult.setPrecondition(TaskResult.PRECONDITION_YES);
    }

    /**
     * 创建Runner
     * @return 结果
     */
    private ImportRunner buildRunner() {
        // 查询任务实体
        ImportPO importPO = SpringHelp.getBean(ImportService.class).findById(taskContext.getNodeId());
        // 处理作业环境变量
        importPO.setFilePath(ContextParamParser.parseParam(importPO.getFilePath(), taskContext));
        importPO.setImportTableName(ContextParamParser.parseParam(importPO.getImportTableName(), taskContext));
        importPO.setImportBatchFields(ContextParamParser.parseParam(importPO.getImportBatchFields(), taskContext));
        // 构建Runner
        return new ImportRunner(new ImportConfigure(importPO), taskContext);
    }

}

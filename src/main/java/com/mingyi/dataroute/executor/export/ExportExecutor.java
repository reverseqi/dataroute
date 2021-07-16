package com.mingyi.dataroute.executor.export;

import com.mingyi.dataroute.executor.ContextParamParser;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.persistence.node.export.entity.ExportPO;
import com.mingyi.dataroute.persistence.node.export.service.ExportService;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * 导出组件
 * @author vbrug
 * @since 1.0.0
 */
public class ExportExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExportExecutor.class);

    private final TaskContext     taskContext;
    private       ExportConfigure configure;

    public ExportExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        TaskResult taskResult = TaskResult.newInstance(taskContext.getTaskId());
        // 01-构建执行器
        ExportRunner exportRunner = this.buildRunner();

        // 02-判断是否有新数据
        if (!exportRunner.hasExportData()) {
            logger.info("【{}--{}】，当前无导出数据", taskContext.getTaskId(), taskContext.getTaskName());
            // 无数据，另行处理
            return taskResult.setPrecondition(TaskResult.PRECONDITION_TWO);
        }

        // 03-输出数据到文件
        exportRunner.sinkToFile();

        // 04-设置输出变量
        taskContext.getData().put(StringUtils.hasText(
                configure.getPo().getResultParamName()) ? configure.getPo().getResultParamName() : ExportConfigure.RESULT_PARAM_NAME,
                configure.getPo().getFilePath());

        return taskResult.setPrecondition(TaskResult.PRECONDITION_YES).setRemark(StringUtils.replacePlaceholder("此次共导出{}条记录",
                configure.getExportAmount()));
    }

    /**
     * 创建Runner
     * @return 结果
     */
    private ExportRunner buildRunner() throws FileNotFoundException {
        // 查询任务实体
        ExportPO exportPO = SpringHelp.getBean(ExportService.class).findById(taskContext.getNodeId());
        // 处理作业环境变量
        exportPO.setFilePath(ContextParamParser.parseParam(exportPO.getFilePath(), taskContext));
        exportPO.setExportSql(ContextParamParser.parseParam(exportPO.getExportSql(), taskContext));
        exportPO.setExportBatchFields(ContextParamParser.parseParam(exportPO.getExportBatchFields(), taskContext));
        // 构建Runner
        configure = new ExportConfigure(exportPO);
        return new ExportRunner(configure, taskContext);
    }


}

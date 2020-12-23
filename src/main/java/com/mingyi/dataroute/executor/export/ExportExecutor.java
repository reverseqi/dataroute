package com.mingyi.dataroute.executor.export;

import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.task.export.po.ExportPO;
import com.mingyi.dataroute.persistence.task.export.service.ExportService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExportExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ExportExecutor.class);

    private final TaskContext taskContext;
    private final JobContext jobContext;

    public ExportExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.jobContext = taskContext.getJobContext();
    }

    @Override
    public void execute() throws Exception {
        // 01-查询任务详情
        ExportPO exportPO = SpringHelp.getBean(ExportService.class).findById(jobContext.getProcessId(), taskContext.getNodeId());

        // 02-环境变量解析
        exportPO.setFilePath(ParamParser.parseParam(exportPO.getFilePath(), new ParamTokenHandler(taskContext)));

        // 03-导出数据
        new ExportRunner(exportPO, taskContext).run();
    }


}
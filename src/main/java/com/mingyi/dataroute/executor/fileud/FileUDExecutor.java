package com.mingyi.dataroute.executor.fileud;

import com.mingyi.dataroute.executor.ContextParamParser;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.persistence.node.fileud.po.FileUDPO;
import com.mingyi.dataroute.persistence.node.fileud.service.FileUDService;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件上传下载执行器
 * @author vbrug
 * @since 1.0.0
 */
public class FileUDExecutor implements Executor {
    private static final Logger logger = LoggerFactory.getLogger(FileUDExecutor.class);

    private final TaskContext     taskContext;
    private       FileUDConfigure configure;

    public FileUDExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        TaskResult taskResult = TaskResult.newInstance(taskContext.getTaskId());
        // 01-执行文件传输
        this.buildRunner().udFile();

        // 02-设置输出变量
        taskContext.getData().put(
                StringUtils.hasText(configure.getPo().getResultParamName()) ? configure.getPo().getResultParamName() : FileUDConfigure.RESULT_PARAM_NAME,
                configure.getPo().getTargetPath());
       
        return taskResult.setRemark(StringUtils.replacePlaceholder("将文件{} {} 到{}",
                configure.getPo().getSourcePath(), configure.getPo().getUdType(), configure.getPo().getTargetPath()));
    }


    /**
     * 创建Runner
     * @return 结果
     */
    private FileUDRunner buildRunner() {
        // 查询任务实体
        FileUDPO fileUDPO = SpringHelp.getBean(FileUDService.class).findById(taskContext.getNodeId());
        // 处理作业环境变量
        fileUDPO.setSourcePath(ContextParamParser.parseParam(fileUDPO.getSourcePath(), taskContext));
        fileUDPO.setTargetPath(ContextParamParser.parseParam(fileUDPO.getTargetPath(), taskContext));
        // 构建Runner
        configure = new FileUDConfigure(fileUDPO);
        return new FileUDRunner(configure, taskContext);
    }

}

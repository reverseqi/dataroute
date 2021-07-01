package com.mingyi.dataroute.executor.fileud;

import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.node.fileud.po.FileUDPO;
import com.mingyi.dataroute.persistence.node.fileud.service.FileUDService;
import com.mingyi.dataroute.persistence.resource.machine.po.MachinePO;
import com.mingyi.dataroute.persistence.resource.machine.service.MachineService;
import com.vbrug.fw4j.common.third.help.SFTPHelp;
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

    public static final String UD_TYPE_U = "U";             // 上传
    public static final String UD_TYPE_D = "D";             // 下载

    private static final Logger logger = LoggerFactory.getLogger(FileUDExecutor.class);

    private final TaskContext taskContext;

    public FileUDExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        TaskResult taskResult = TaskResult.newInstance(taskContext.getTaskId());
        // 01-查询文件上传下载任务信息
        FileUDPO po       = SpringHelp.getBean(FileUDService.class).findById(taskContext.getNodeId());
        SFTPHelp sftpHelp = this.getSFTPHelp(po);
        try {
            sftpHelp.connect();

            // 02-处理环境参数
            String sourcePath = ParamParser.parseParam(po.getSourcePath(), new ParamTokenHandler(taskContext));
            String targetPath = ParamParser.parseParam(po.getTargetPath(), new ParamTokenHandler(taskContext));

            // 03-执行任务
            if (UD_TYPE_U.equalsIgnoreCase(po.getUdType())) {
                sftpHelp.upload(sourcePath, targetPath);
                logger.info("【{}--{}】，开始上传[{}]到{}[{}]", taskContext.getTaskId(), taskContext.getTaskName(), sourcePath, po.getMachineId(), targetPath);
            } else {
                sftpHelp.download(sourcePath, targetPath);
                logger.info("【{}--{}】，开始下载{}--[{}]到[{}]", taskContext.getTaskId(), taskContext.getTaskName(), po.getMachineId(), sourcePath, targetPath);
            }
            taskResult.getData().put(po.getResultParamName(), targetPath);
            return taskResult.setRemark(StringUtils.replacePlaceholder("将文件{}推送 {} 到{}", sourcePath, po.getUdType(), targetPath));
        } finally {
            sftpHelp.close();
        }
    }


    private SFTPHelp getSFTPHelp(FileUDPO fileUDPO) {
        MachinePO machinePO = SpringHelp.getBean(MachineService.class).findById(fileUDPO.getMachineId());
        return new SFTPHelp(machinePO.getHost(), machinePO.getPort(), machinePO.getUsername(), machinePO.getPassword());
    }
}

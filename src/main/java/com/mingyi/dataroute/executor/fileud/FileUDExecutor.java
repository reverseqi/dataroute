package com.mingyi.dataroute.executor.fileud;

import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.resource.machine.po.MachinePO;
import com.mingyi.dataroute.persistence.resource.machine.service.MachineService;
import com.mingyi.dataroute.persistence.task.fileud.po.FileUDPO;
import com.mingyi.dataroute.persistence.task.fileud.service.FileUDService;
import com.vbrug.fw4j.common.third.help.SFTPHelp;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件上传下载执行器
 *
 * @author vbrug
 * @since 1.0.0
 */
public class FileUDExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(FileUDExecutor.class);

    private final TaskContext taskContext;
    private final JobContext jobContext;

    public FileUDExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.jobContext = taskContext.getJobContext();
    }

    @Override
    public void execute() throws Exception {
        // 01-查询文件上传下载任务信息
        FileUDPO fileUDPO = SpringHelp.getBean(FileUDService.class).findById(jobContext.getProcessId(), taskContext.getNodeId());
        SFTPHelp sftpHelp = this.getSFTPHelp(fileUDPO);
        try {
            sftpHelp.connect();

            // 02-处理环境参数
            String sourcePath = ParamParser.parseParam(fileUDPO.getSourcePath(), new ParamTokenHandler(taskContext));
            String targetPath = ParamParser.parseParam(fileUDPO.getTargetPath(), new ParamTokenHandler(taskContext));

            // 03-执行任务
            if ("U".equals(fileUDPO.getUdType())) {
                logger.info("【{}--{}】，开始上传[{}]到{}[{}]", taskContext.getId(), taskContext.getNodeName(), sourcePath, fileUDPO.getMachineId(), targetPath);
                sftpHelp.upload(sourcePath, targetPath);
                taskContext.getDataMap().put(ExecutorConstants.UPLOAD_FILE_PATH, targetPath);
            } else {
                logger.info("【{}--{}】，开始下载{}--[{}]到[{}]", taskContext.getId(), taskContext.getNodeName(), fileUDPO.getMachineId(), sourcePath, targetPath);
                sftpHelp.download(sourcePath, targetPath);
                taskContext.getDataMap().put(ExecutorConstants.DOWNLOAD_FILE_PATH, targetPath);
            }
        } finally {
            sftpHelp.close();
        }
    }


    private SFTPHelp getSFTPHelp(FileUDPO fileUDPO) {
        MachinePO machinePO = SpringHelp.getBean(MachineService.class).findById(fileUDPO.getMachineId());
        return new SFTPHelp(machinePO.getHost(), machinePO.getPort(), machinePO.getUsername(), machinePO.getPassword());
    }
}

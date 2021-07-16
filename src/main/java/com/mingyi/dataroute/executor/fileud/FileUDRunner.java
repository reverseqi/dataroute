package com.mingyi.dataroute.executor.fileud;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.mingyi.dataroute.persistence.resource.machine.po.MachinePO;
import com.mingyi.dataroute.persistence.resource.machine.service.MachineService;
import com.vbrug.fw4j.common.util.third.file.FTPFileTransfer;
import com.vbrug.fw4j.common.util.third.file.FileTransfer;
import com.vbrug.fw4j.common.util.third.file.SFTPFileTransfer;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件上传下载执行器
 * @author vbrug
 * @since 1.0.0
 */
public class FileUDRunner {

    private static final Logger logger = LoggerFactory.getLogger(FileUDRunner.class);

    private final FileUDConfigure configure;
    private final TaskContext     taskContext;

    FileUDRunner(FileUDConfigure configure, TaskContext taskContext) {
        this.configure = configure;
        this.taskContext = taskContext;
    }

    /**
     * 上传下载文件
     * @throws Exception 异常
     */
    public void udFile() throws Exception {
        FileTransfer fileTransfer = this.getFileTransfer();
        try {
            fileTransfer.connect();
            switch (configure.getPo().getUdType()) {
                case FileUDConfigure.UD_TYPE_DOWNLOAD:
                    logger.info("{}--{} ->> 开始从{}下载{}文件到本地{}", taskContext.getTaskId(), taskContext.getTaskName(),
                            configure.getMachineName(), configure.getPo().getSourcePath(), configure.getPo().getTargetPath());
                    fileTransfer.download(configure.getPo().getTargetPath(), configure.getPo().getSourcePath());
                    logger.info("{}--{} ->> 完成从{}下载{}文件到本地{}", taskContext.getTaskId(), taskContext.getTaskName(),
                            configure.getMachineName(), configure.getPo().getSourcePath(), configure.getPo().getTargetPath());
                    break;
                case FileUDConfigure.UD_TYPE_UPLOAD:
                    logger.info("{}--{} ->> 开始上传本地文件{}到{}服务器，上传地址{}", taskContext.getTaskId(), taskContext.getTaskName(),
                             configure.getPo().getSourcePath(), configure.getMachineName(),configure.getPo().getTargetPath());
                    fileTransfer.upload(configure.getPo().getSourcePath(), configure.getPo().getTargetPath());
                    logger.info("{}--{} ->> 完成上传本地文件{}到{}服务器，上传地址{}", taskContext.getTaskId(), taskContext.getTaskName(),
                            configure.getPo().getSourcePath(), configure.getMachineName(),configure.getPo().getTargetPath());
                    break;
                default:
                    throw new DataRouteException("不支持的文件传输类型:{}", configure.getPo().getUdType());
            }
        } finally {
            fileTransfer.close();
        }
    }

    /**
     * 获取文件传输对象
     * @return 结果
     */
    private FileTransfer getFileTransfer() {
        MachinePO machinePO = SpringHelp.getBean(MachineService.class).findById(configure.getPo().getMachineId());
        configure.setMachineName(machinePO.getName());
        switch (machinePO.getProtocolType()) {
            case FileUDConfigure.UD_PROTOCOL_TYPE_FTP:
                return new FTPFileTransfer(machinePO.getHost(), machinePO.getPort(), machinePO.getUsername(), machinePO.getPassword());
            case FileUDConfigure.UD_PROTOCOL_TYPE_SSH:
                return new SFTPFileTransfer(machinePO.getHost(), machinePO.getPort(), machinePO.getUsername(), machinePO.getPassword());
            default:
                throw new DataRouteException("不支持的文件传输协议:{}", machinePO.getProtocolType());
        }
    }


}

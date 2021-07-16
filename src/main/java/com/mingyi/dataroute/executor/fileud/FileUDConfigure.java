package com.mingyi.dataroute.executor.fileud;

import com.mingyi.dataroute.persistence.node.fileud.po.FileUDPO;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class FileUDConfigure {

    public static final String UD_TYPE_UPLOAD   = "UPLOAD";               // 上传
    public static final String UD_TYPE_DOWNLOAD = "DOWNLOAD";             // 下载

    public static final String UD_PROTOCOL_TYPE_FTP = "FTP";
    public static final String UD_PROTOCOL_TYPE_SSH = "SSH";

    public static final String RESULT_PARAM_NAME = "transfer_file_path";

    private final FileUDPO po;
    private       String   machineName;                                 // 文件传输服务器名称


    FileUDConfigure(FileUDPO po) {
        this.po = po;
    }

    public FileUDPO getPo() {
        return po;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}

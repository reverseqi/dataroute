package com.mingyi.dataroute.persistence.node.fileud.po;

/**
 * 文件上传下载实体
 *
 * @author vbrug
 * @since 1.0.0
 */
public class FileUDPO {

    private Integer processId;                         // 流程ID
    private Integer nodeId;                            // 节点ID
    private String udType;                             // 操作类型（D--DOWNLOAD， U--UPLOAD）
    private String sourcePath;                         // 源文件地址
    private String targetPath;                         // 目标文件地址
    private Integer machineId;                         // 机器ID
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数
    private String remark;                             // 备注

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getUdType() {
        return udType;
    }

    public void setUdType(String udType) {
        this.udType = udType;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Integer getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(Integer maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

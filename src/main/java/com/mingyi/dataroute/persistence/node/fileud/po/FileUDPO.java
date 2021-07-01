package com.mingyi.dataroute.persistence.node.fileud.po;

/**
 * 文件上传下载实体
 * @author vbrug
 * @since 1.0.0
 */
public class FileUDPO {

    private Integer id;                             // 节点ID
    private String  udType;                             // 操作类型（D--DOWNLOAD， U--UPLOAD）
    private String  sourcePath;                         // 源文件地址
    private String  targetPath;                         // 目标文件地址
    private Integer machineId;                          // 机器ID
    private String  resultParamName;                    // 结果参数名称
    private String  remark;                             // 备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getResultParamName() {
        return resultParamName;
    }

    public void setResultParamName(String resultParamName) {
        this.resultParamName = resultParamName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

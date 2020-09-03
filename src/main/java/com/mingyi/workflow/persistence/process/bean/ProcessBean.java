package com.mingyi.workflow.persistence.process.bean;

import java.io.Serializable;

public class ProcessBean implements Serializable {

    private static final long serialVersionUID = -762553837279635224L;

    private String id;                                 // id
    private String name;                               // 名称
    private String deployFlag;                         // 发布标志（0-否，1-是）
    private String remark;                             // 描述
    private String createUser;                         // 创建用户
    private String createTime;                         // 创建时间
    private String updateUser;                         // 修改用户
    private String updateTime;                         // 修改时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(String deployFlag) {
        this.deployFlag = deployFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}

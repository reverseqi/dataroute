package com.mingyi.dataroute.persistence.resource.machine.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class MachinePO {

    private Integer id;                                // key
    private String  name;                               // 名称
    private String  host;                               // 地址
    private Integer port;                              // 端口
    private String  protocolType;                       // 协议类型（SSH、FTP）
    private String  username;                           // 用户名
    private String  password;                           // 密码
    private Integer stopFlag;                          // 停用状态（0-否，1-是）
    private String  remark;                             // 描述
    private String  createUser;                         // 创建用户
    private String  createTime;                         // 创建时间
    private String  updateUser;                         // 修改用户
    private String  updateTime;                         // 修改时间

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(Integer stopFlag) {
        this.stopFlag = stopFlag;
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

package com.mingyi.dataroute.persistence.resource.datasource.po;

import java.io.Serializable;

/**
 * 数据源实体
 * @author vbrug
 * @since 1.0.0
 */
public class DataSourcePO implements Serializable {

    private static final long serialVersionUID = 8273025228206765276L;

    private Integer id;                                // ID
    private String name;                               // 数据库名称
    private String host;                               // 地址
    private Integer port;                              // 端口号
    private String dbname;                             // 数据库名称
    private String username;                           // 用户名
    private String password;                           // 密码
    private String jdbcParams;                         // JDBC连接参数
    private String type;                               // 数据库类型
    private Integer stopFlag;                          // 停用标志（0-否，1-是）
    private String remark;                             // 描述
    private String createUser;                         // 创建用户
    private String createTime;                         // 创建时间
    private String updateUser;                         // 修改用户
    private String updateTime;                         // 修改时间

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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
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

    public String getJdbcParams() {
        return jdbcParams;
    }

    public void setJdbcParams(String jdbcParams) {
        this.jdbcParams = jdbcParams;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

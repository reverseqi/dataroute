package com.mingyi.dataroute.persistence.scheduler.entity;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class SchedulerPO {
    private Integer id;                                // 主键
    private String name;                               // 作业名称
    private Integer processId;                         // 流程ID
    private Long jobId;                             // 任务ID
    private String crontab;                            // crontab表达式
    private String startTime;                          // 开始时间
    private String endTime;                            // 结束时间
    private Integer relyLastJob;                           // 依赖作业, 0代表无依赖, 1代表依赖上次作业执行
    private Integer actJob;                            // 执行中的作业数量
    private String globalParams;                       // 全局参数
    private Integer stopFlag;                          // 停用标志（0-否，1-是）
    private String remark;                             // 描述
    private String createUser;                         // 创建用户
    private String createTime;                         // 创建时间
    private String updateUser;                         // 修改用户
    private String updateTime;                         // 修改时间
    private int state;                              // 执行状态

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
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

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getCrontab() {
        return crontab;
    }

    public void setCrontab(String crontab) {
        this.crontab = crontab;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getRelyLastJob() {
        return relyLastJob;
    }

    public void setRelyLastJob(Integer relyLastJob) {
        this.relyLastJob = relyLastJob;
    }

    public Integer getActJob() {
        return actJob;
    }

    public void setActJob(Integer actJob) {
        this.actJob = actJob;
    }

    public String getGlobalParams() {
        return globalParams;
    }

    public void setGlobalParams(String globalParams) {
        this.globalParams = globalParams;
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

package com.mingyi.dataroute.persistence.task.extract.po;

import java.io.Serializable;

/**
 * 数据抽取PO
 *
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractPO implements Serializable {

    private static final long serialVersionUID = 1748636713561368876L;

    private Integer processId;                         // 流程ID
    private Integer nodeId;                            // 节点ID
    private Integer originDatasource;                  // 源数据库
    private String originTable;                        // 源表
    private Integer targetDatasource;                  // 目标数据库
    private String targetTable;                        // 目标表
    private String fields;                             // 抽取字段(逗号分割)
    private String triggerField;                       // 触发字段
    private String lastTriggerMaxValue;                // 上次触发最大值
    private Integer bufferSize;                        // 缓存size
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

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

    public Integer getOriginDatasource() {
        return originDatasource;
    }

    public void setOriginDatasource(Integer originDatasource) {
        this.originDatasource = originDatasource;
    }

    public String getOriginTable() {
        return originTable;
    }

    public void setOriginTable(String originTable) {
        this.originTable = originTable;
    }

    public Integer getTargetDatasource() {
        return targetDatasource;
    }

    public void setTargetDatasource(Integer targetDatasource) {
        this.targetDatasource = targetDatasource;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getTriggerField() {
        return triggerField;
    }

    public void setTriggerField(String triggerField) {
        this.triggerField = triggerField;
    }

    public String getLastTriggerMaxValue() {
        return lastTriggerMaxValue;
    }

    public void setLastTriggerMaxValue(String lastTriggerMaxValue) {
        this.lastTriggerMaxValue = lastTriggerMaxValue;
    }

    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
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
}

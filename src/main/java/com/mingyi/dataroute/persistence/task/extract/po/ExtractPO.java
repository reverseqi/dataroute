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
    private String extraFields;                        // 保存的参数字段
    private String triggerField;                       // 触发字段
    private String defaultCond;                       // 默认查询条件
    private String lastTriggerValue;                   // 上次触发值
    private String primaryKey;                         // 主键字段
    private Integer trimWhitespace;                    // 去除前后空格（0-否，1-是）
    private String handleType;                         // 处理类型（TRUNCATE, APPEND）
    private Integer bufferInsertSize;                        // 缓存size
    private Integer bufferFetchSize;                        // 缓存size
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数
    private Integer producerNumber;                    // 生产者数量
    private Integer consumerNumber;                    // 消费者数量


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

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getExtraFields() {
        return extraFields;
    }

    public void setExtraFields(String extraFields) {
        this.extraFields = extraFields;
    }

    public String getTriggerField() {
        return triggerField;
    }

    public void setTriggerField(String triggerField) {
        this.triggerField = triggerField;
    }

    public String getDefaultCond() {
        return defaultCond;
    }

    public void setDefaultCond(String defaultCond) {
        this.defaultCond = defaultCond;
    }

    public String getLastTriggerValue() {
        return lastTriggerValue;
    }

    public void setLastTriggerValue(String lastTriggerValue) {
        this.lastTriggerValue = lastTriggerValue;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getTrimWhitespace() {
        return trimWhitespace;
    }

    public void setTrimWhitespace(Integer trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
    }

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public Integer getBufferInsertSize() {
        return bufferInsertSize;
    }

    public void setBufferInsertSize(Integer bufferInsertSize) {
        this.bufferInsertSize = bufferInsertSize;
    }

    public Integer getBufferFetchSize() {
        return bufferFetchSize;
    }

    public void setBufferFetchSize(Integer bufferFetchSize) {
        this.bufferFetchSize = bufferFetchSize;
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

    public Integer getProducerNumber() {
        return producerNumber;
    }

    public void setProducerNumber(Integer producerNumber) {
        this.producerNumber = producerNumber;
    }

    public Integer getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(Integer consumerNumber) {
        this.consumerNumber = consumerNumber;
    }
}

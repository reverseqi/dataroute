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
    private String triggerField;                       // 触发条件
    private String defaultCond;                        // 默认查询条件
    private Integer trimWhitespace;                    // 去除前后空格（0-否，1-是）
    private String handleType;                         // 处理类型（TRUNCATE, APPEND）
    private Integer bufferFetchSize;                   // 缓存抽取数量
    private Integer bufferInsertSize;                  // 插入缓存数量
    private String params;                             // 参数
    private Integer producerNumber;                    // 生产者数量
    private Integer consumerNumber;                    // 消费者数量
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数

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

    public Integer getBufferFetchSize() {
        return bufferFetchSize;
    }

    public void setBufferFetchSize(Integer bufferFetchSize) {
        this.bufferFetchSize = bufferFetchSize;
    }

    public Integer getBufferInsertSize() {
        return bufferInsertSize;
    }

    public void setBufferInsertSize(Integer bufferInsertSize) {
        this.bufferInsertSize = bufferInsertSize;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
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

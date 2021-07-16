package com.mingyi.dataroute.persistence.node.extract.po;

import java.io.Serializable;

/**
 * 数据抽取PO
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractPO implements Serializable {

    private static final long serialVersionUID = 1748636713561368876L;


    private Integer id;                                 // 节点ID
    private Integer originDatasource;                   // 源数据库
    private String  originTable;                        // 源表【支持作业环境变量解析】
    private Integer targetDatasource;                   // 目标数据库
    private String targetTable;                        // 目标表【支持作业环境变量解析】
    private String extractFields;                       // 抽取字段(逗号分割)
    private String extractBatchFields;                  // 抽取批次字段【支持作业环境变量解析】
    private String extractCondField;                   // 抽取条件字段
    private String  defaultCond;                        // 默认查询条件
    private String  sinkDbType;                         // 入库类型（TRUNCATE, APPEND）
    private String  params;                             // JSON格式，可支持参数buffer_fetch_size--每次取出数量 buffer_insert_size--每次插入数量
    private Integer producerNumber;                     // 生产者数量
    private Integer consumerNumber;                     // 消费者数量
    private String  remark;                             // 备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getExtractFields() {
        return extractFields;
    }

    public void setExtractFields(String extractFields) {
        this.extractFields = extractFields;
    }

    public String getExtractBatchFields() {
        return extractBatchFields;
    }

    public void setExtractBatchFields(String extractBatchFields) {
        this.extractBatchFields = extractBatchFields;
    }

    public String getExtractCondField() {
        return extractCondField;
    }

    public void setExtractCondField(String extractCondField) {
        this.extractCondField = extractCondField;
    }

    public String getDefaultCond() {
        return defaultCond;
    }

    public void setDefaultCond(String defaultCond) {
        this.defaultCond = defaultCond;
    }

    public String getSinkDbType() {
        return sinkDbType;
    }

    public void setSinkDbType(String sinkDbType) {
        this.sinkDbType = sinkDbType;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

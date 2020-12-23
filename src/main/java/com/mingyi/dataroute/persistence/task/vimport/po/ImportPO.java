package com.mingyi.dataroute.persistence.task.vimport.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ImportPO {

    private Integer processId;                         // 流程ID
    private Integer nodeId;                            // 节点ID
    private String filePath;                           // 文件地址
    private String parseType;                          // 解析类型（0-JSON，1-CSV，2-CSVWithNames，3-TabSeparated, 4-TabSeparatedWithNames）
    private String fields;                        // 列名称（以逗号分割）
    private Integer datasourceId;                      // 数据源ID
    private String tableName;                          // 导入表名
    private String handleType;                         // 处理类型（TRUNCATE， APPEND）
    private String extraFields;                        // 额外的参数字段
    private Integer bufferSize;                        // 缓存size
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数
    private String remark;                             // 备注
    private Integer producerNumber;                    // 生产者数量
    private Integer consumerNumber;                    // 消费者数量

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getParseType() {
        return parseType;
    }

    public void setParseType(String parseType) {
        this.parseType = parseType;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getExtraFields() {
        return extraFields;
    }

    public void setExtraFields(String extraFields) {
        this.extraFields = extraFields;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

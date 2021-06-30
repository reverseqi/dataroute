package com.mingyi.dataroute.persistence.node.export.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExportPO {

    private Integer processId;                         // 流程ID
    private Integer nodeId;                            // 节点ID
    private Integer datasourceId;                      // 数据源ID
    private String sql;                                // 导出SQL
    private String filePath;                           // 导出文件路径
    private Integer bufferFetchSize;                   // 每次获取数量
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数
    private Integer producerNumber;                    // 线程数量
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

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

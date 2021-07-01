package com.mingyi.dataroute.persistence.node.export.entity;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExportPO {

    private Integer id;                                // 节点ID
    private Integer datasourceId;                      // 数据源ID
    private String  sql;                               // 导出SQL
    private String  filePath;                          // 导出文件路径
    private Integer bufferFetchSize;                   // 每次获取数量
    private Integer producerNumber;                    // 线程数量
    private Integer consumerNumber;                    // 消费者数量
    private String  resultParamName;                   // 结果参数名
    private String  remark;                            // 备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getResultParamName() {
        return resultParamName;
    }

    public void setResultParamName(String resultParamName) {
        this.resultParamName = resultParamName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

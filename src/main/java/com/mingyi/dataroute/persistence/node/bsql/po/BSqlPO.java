package com.mingyi.dataroute.persistence.node.bsql.po;

import java.io.Serializable;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlPO implements Serializable {

    private static final long serialVersionUID = -2131185449818532438L;

    private Integer processId;                         // 流程ID
    private Integer nodeId;                            // 节点ID
    private Integer datasourceId;                      // 数据源ID
    private String  bsqlPath;                          // 批sql路径
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

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getBsqlPath() {
        return bsqlPath;
    }

    public void setBsqlPath(String bsqlPath) {
        this.bsqlPath = bsqlPath;
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

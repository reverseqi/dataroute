package com.mingyi.dataroute.persistence.task.http.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class HttpPO {

    private Integer processId;                         // 流程ID
    private Integer nodeId;                            // 节点ID
    private String url;                                // 请求路径
    private String params;                             // 请求参数
    private String configure;                          // 配置参数
    private String header;                             // 请求头
    private Integer retryInterval;                     // 重试间隔（分）
    private Integer maxRetryTimes;                     // 最大重试次数
    private String remark;                             // 日志

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getConfigure() {
        return configure;
    }

    public void setConfigure(String configure) {
        this.configure = configure;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
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
}



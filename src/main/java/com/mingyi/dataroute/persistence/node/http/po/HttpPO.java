package com.mingyi.dataroute.persistence.node.http.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class HttpPO {

    private Integer id;                                // 节点ID
    private String url;                                // 请求路径
    private String sourceType;                         // 数据源类型（CONTEXT-作业环境变量，DB-数据库）
    private String params;                             // 请求参数,JSON格式【支持作业环境变量解析】
    private String configure;                          // 配置参数
    private String header;                             // 请求头
    private String handleType;                         // 处理类型（SYNC--同步，ASYNC--异步）
    private String sinkType;                           // 结果输出类型（CONTEXT-作业环境变量，MAP-集合，KAFKA-消息队列）
    private String resultParamName;                    // 环境变量名称（空时默认为resultFilePath）
    private String remark;                             // 备注

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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

    public String getHandleType() {
        return handleType;
    }

    public void setHandleType(String handleType) {
        this.handleType = handleType;
    }

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
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



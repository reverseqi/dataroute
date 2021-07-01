package com.mingyi.dataroute.persistence.node.http.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class HttpPO {

    private Integer id;                            // 节点ID
    private String  url;                                // 请求路径
    private String  params;                             // 请求参数
    private String  configure;                          // 配置参数
    private String  header;                             // 请求头
    private String  remark;                             // 日志

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}



package com.mingyi.dataroute.persistence.node.bsql.entity;

import java.io.Serializable;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlPO implements Serializable {

    private static final long serialVersionUID = -2131185449818532438L;

    private Integer id;                                // 节点ID
    private Integer datasourceId;                      // 数据源ID
    private String  bsqlPath;                          // 批sql路径
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

    public String getBsqlPath() {
        return bsqlPath;
    }

    public void setBsqlPath(String bsqlPath) {
        this.bsqlPath = bsqlPath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

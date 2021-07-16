package com.mingyi.dataroute.executor.bsql;

import com.mingyi.dataroute.db.StatementType;

/**
 * BSQL实体类
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlBean {

    private StatementType statementType;
    private Integer       id;
    private Integer       databaseId;
    private String        description;
    private String        sql;

    public StatementType getStatementType() {
        return statementType;
    }

    public void setStatementType(StatementType statementType) {
        this.statementType = statementType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}

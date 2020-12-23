package com.mingyi.dataroute.executor.bsql;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlBean {

    public enum StatementType {
        DELETE("DELETE"), INSERT("INSERT"), SELECT("SELECT"), UPDATE("UPDATE");
        public final String value;
        StatementType(String value){this.value = value;}

        public static StatementType getByValue(String statementType) {
            for (StatementType value : values()) {
                if (value.value.equalsIgnoreCase(statementType))
                    return value;
            }
            return null;
        }
    }

    private StatementType statementType;
    private Integer id;
    private Integer databaseId;
    private String description;
    private String sql;

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

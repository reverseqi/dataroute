package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.persistence.task.vimport.po.ImportPO;

import java.sql.SQLException;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ImportConsumerDO {

    private String batchInsertSql;
    private JobSqlRunner sqlRunner;
    private JobDataSource dataSource;
    private String fields;

    ImportConsumerDO(ImportPO po, JobDataSource jobDataSource) {
        this.dataSource = jobDataSource;
        try {
            this.sqlRunner = new JobSqlRunner(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.batchInsertSql = dataSource.getDialect().buildInsertSQL(po.getTableName(), po.getFields());
        this.fields = po.getFields();
    }

    public String getBatchInsertSql() {
        return batchInsertSql;
    }

    public void setBatchInsertSql(String batchInsertSql) {
        this.batchInsertSql = batchInsertSql;
    }

    public JobSqlRunner getSqlRunner() {
        return sqlRunner;
    }

    public void setSqlRunner(JobSqlRunner sqlRunner) {
        this.sqlRunner = sqlRunner;
    }

    public JobDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(JobDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
}

package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractProducerDO {

    private String extractSql;
    private SqlRunner sqlRunner;
    private String triggerField;
    private String primaryKeyField;
    private JobDataSource dataSource;
    private ExtractPO extractPO;
    private AtomicLong count;

    ExtractProducerDO(ExtractPO po, JobDataSource jobDataSource) {
        this.dataSource = jobDataSource;
        this.extractPO = po;
        try {
            this.sqlRunner = new SqlRunner(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.extractSql = dataSource.getDialect().getExtractSql(po.getOriginTable(), po.getFields(), po.getTriggerField(), po.getDefaultCond());
        this.triggerField = po.getTriggerField();
        this.primaryKeyField = po.getPrimaryKey();
        this.count = new AtomicLong(0);
    }

    public AtomicLong getCount() {
        return count;
    }

    public String getExtractSql() {
        return extractSql;
    }

    public void setExtractSql(String extractSql) {
        this.extractSql = extractSql;
    }

    public SqlRunner getSqlRunner() {
        return sqlRunner;
    }

    public void setSqlRunner(SqlRunner sqlRunner) {
        this.sqlRunner = sqlRunner;
    }

    public String getTriggerField() {
        return triggerField;
    }

    public void setTriggerField(String triggerField) {
        this.triggerField = triggerField;
    }

    public String getPrimaryKeyField() {
        return primaryKeyField;
    }

    public void setPrimaryKeyField(String primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
    }

    public JobDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(JobDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ExtractPO getExtractPO() {
        return extractPO;
    }
}

package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConsumerDO {

    private String batchInsertSql;
    private JobSqlRunner sqlRunner;
    private JobDataSource dataSource;
    private String fields;
    private AtomicLong count;

    ExtractConsumerDO(ExtractPO po, JobDataSource jobDataSource) {
        this.dataSource = jobDataSource;
        try {
            this.sqlRunner = new JobSqlRunner(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.batchInsertSql = dataSource.getDialect().getInsertSql(po.getTargetTable(), po.getFields());
        this.fields = po.getFields();
        this.count = new AtomicLong(0);
    }

    public AtomicLong getCount() {
        return count;
    }

    public String getFields() {
        return fields;
    }

    public String getBatchInsertSql() {
        return batchInsertSql;
    }

    public JobSqlRunner getSqlRunner() {
        return sqlRunner;
    }

    public JobDataSource getDataSource() {
        return dataSource;
    }
}

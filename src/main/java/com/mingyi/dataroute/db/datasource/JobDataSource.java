package com.mingyi.dataroute.db.datasource;

import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.db.dialect.DialectFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class JobDataSource extends PooledDataSource {

    private Dialect dialect;

    public JobDataSource() {
        super();
    }

    public JobDataSource(UnpooledDataSource dataSource) {
        super(dataSource);
    }

    public JobDataSource(String driver, String url, String username, String password) {
        super(driver, url, username, password);
    }

    public JobDataSource(String driver, String url, Properties driverProperties) {
        super(driver, url, driverProperties);
    }

    public JobDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
        super(driverClassLoader, driver, url, username, password);
    }

    public JobDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        super(driverClassLoader, driver, url, driverProperties);
    }

    public Dialect getDialect() {
        if (dialect == null) {
            dialect = DialectFactory.createDSDialect(this.getDriver());
        }
        return dialect;
    }

    public JobSqlRunner getSqlRunner() throws SQLException {
        return new JobSqlRunner(this.getConnection());
    }
}

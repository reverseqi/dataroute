package com.mingyi.dataroute.db;

import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class JobSqlRunner extends SqlRunner {

    private final Connection connection;

    public JobSqlRunner(Connection connection) {
        super(connection);
        this.connection = connection;
    }

    public void runBatch(String... sqls) throws SQLException {
        this.connection.setAutoCommit(false);
        Statement stmt = connection.createStatement();
        for (String sql : sqls) {
            stmt.addBatch(sql);
        }
        try {
            stmt.executeBatch();
            this.connection.commit();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                //ignore
            }
        }
        this.connection.setAutoCommit(true);
    }
}

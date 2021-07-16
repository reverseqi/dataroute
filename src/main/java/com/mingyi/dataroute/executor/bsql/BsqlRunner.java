package com.mingyi.dataroute.executor.bsql;

import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.StatementType;
import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.db.dialect.ClickHouseDialect;
import com.mingyi.dataroute.db.dialect.JdbcDriverType;
import com.mingyi.dataroute.exceptions.DataRouteException;
import com.mingyi.dataroute.executor.SQLParserUtils;
import com.mingyi.dataroute.persistence.node.bsql.entity.BSqlPO;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * BSQL
 * @author vbrug
 * @since 1.0.0
 */
public class BsqlRunner {

    private static final Logger logger = LoggerFactory.getLogger(BsqlRunner.class);

    private final        JobDataSource defaultDataSource;
    private final        BSqlPO        bSqlPO;
    private final        TaskContext   taskContext;
    private static final String        CK_ASSERT_FIELD_NAME_STATE       = "state";
    private static final String        CK_ASSERT_FIELD_NAME_COMMAND     = "command";
    private static final String        CK_ASSERT_FIELD_NAME_FAIL_REASON = "latest_fail_reason";

    private static final int CK_ASSERT_STATE_UNFINISHED = 0;
    private static final int CK_ASSERT_STATE_FINISHED   = 1;
    private static final int CK_ASSERT_STATE_EXCEPTION  = 9;

    BsqlRunner(TaskContext taskContext, BSqlPO bSqlPO) throws SQLException {
        this.taskContext = taskContext;
        this.bSqlPO = bSqlPO;
        this.defaultDataSource = DataSourcePool.getInstance().getDataSource(bSqlPO.getDatasourceId());
    }

    /**
     * 执行Sql脚本
     * @param sqlBeanList SQL实体类集合
     */
    public void run(List<BSqlBean> sqlBeanList) throws Exception {
        JobDataSource dataSource = null;
        for (BSqlBean x : sqlBeanList) {
            if (x.getDatabaseId() != null)
                dataSource = DataSourcePool.getInstance().getDataSource(x.getDatabaseId());
            else
                dataSource = this.defaultDataSource;
            if (dataSource.getDialect().getDialectType() == JdbcDriverType.CLICKHOUSE
                    && (x.getStatementType() == StatementType.DELETE || x.getStatementType() == StatementType.UPDATE)) {
                assertCKUDIsDone(x, dataSource);
            } else {
                dataSource.getSqlRunner().run(x.getSql());
            }
            logger.info("【{}--{}】, {} 中执行SQL-->{}", taskContext.getTaskId(), taskContext.getTaskName(),
                    x.getDatabaseId() == null ? bSqlPO.getDatasourceId() : x.getDatabaseId(), x.getSql().substring(0, 100));
        }
    }

    /**
     * 判断clickhouse 的 UPDATE、DELETE操作是否成功
     * @param tableName 判断表名称
     */
    private void assertCKUDIsDone(BSqlBean sqlBean, JobDataSource dataSource) throws SQLException, InterruptedException {
        JobSqlRunner sqlRunner = dataSource.getSqlRunner();
        // 获取sql前后执行时间
        String startDateTime = ckQueryCurrentTime(sqlRunner);
        sqlRunner.run(sqlBean.getSql());
        String endDateTime = ckQueryCurrentTime(sqlRunner);

        // 分析sql操作表
        String tableName = SQLParserUtils.getTableName(sqlBean.getSql());
        String database  = null;
        if (tableName.contains(".")) {
            database = tableName.split(".")[0];
            tableName = tableName.split(".")[1];
        }
        // 构建判断SQL
        String assertFinishSQL = ((ClickHouseDialect) dataSource.getDialect()).buildUDAssertFinishSQL(database, tableName,
                sqlBean.getStatementType(), startDateTime, endDateTime);


        // 判断sql是否已完成
        while (true) {
            Map<String, Object> assertResultMap = sqlRunner.selectOne(assertFinishSQL);
            Integer             state           = Integer.valueOf(assertResultMap.get(CK_ASSERT_FIELD_NAME_STATE).toString());
            switch (state) {
                case CK_ASSERT_STATE_FINISHED:
                    break;
                case CK_ASSERT_STATE_EXCEPTION:
                    String failReason = assertResultMap.get(CK_ASSERT_FIELD_NAME_FAIL_REASON).toString();
                    throw new DataRouteException(StringUtils.replacePlaceholder("BSQL执行发生异常，异常SQL-->> {}, 异常原因-->> {}",
                            sqlBean.getSql(), failReason));
                case CK_ASSERT_STATE_UNFINISHED:
                default:
                    TimeUnit.MICROSECONDS.sleep(500L);
            }
        }
    }

    /**
     * 获取Clickhouse数据库当前时间
     * @param sqlRunner SQL执行器
     * @return 数据库时间
     * @throws SQLException 异常
     */
    private String ckQueryCurrentTime(JobSqlRunner sqlRunner) throws SQLException {
        return sqlRunner.selectOne("select toString(now()) db_time").get("db_time").toString();
    }


}

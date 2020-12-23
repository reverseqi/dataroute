package com.mingyi.dataroute.executor.bsql;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.executor.SQLParser;
import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlRunner {

    private static final Logger logger = LoggerFactory.getLogger(BSqlRunner.class);


    private final SqlRunner defaultSqlRunner;
    private final BSqlPO bSqlPO;
    private final TaskContext taskContext;

    BSqlRunner(TaskContext taskContext, BSqlPO bSqlPO) {
        this.taskContext = taskContext;
        this.bSqlPO = bSqlPO;
        this.defaultSqlRunner = taskContext.getJobContext().getSqlRunner(bSqlPO.getDatasourceId());
    }

    /**
     * 执行Sql脚本
     */
    public void run(List<BSqlBean> sqlBeanList) throws SQLException {
        SqlRunner sqlRunner = null;
        for (BSqlBean x : sqlBeanList) {
            if (x.getDatabaseId() != null)
                sqlRunner = this.taskContext.getJobContext().getSqlRunner(x.getDatabaseId());
            else
                sqlRunner = this.defaultSqlRunner;
            sqlRunner.run(SQLParser.parseToken(x.getSql(), new ParamTokenHandler(taskContext)));
            logger.info("【{}--{}】, 执行SQL-->{} execute in {}", taskContext.getId(), taskContext.getNodeName(), x.getSql(),
                    x.getDatabaseId() == null ? bSqlPO.getDatasourceId() : x.getDatabaseId());
        }
    }


    /**
     * 判断clickhouse 的 UPDATE、DELETE操作是否成功
     *
     * @param tableName 判断表名称
     */
    // TODO
    public static void assertCKUDIsDone(String tableName) {
        while (true) {
//            int result = service.selectUDSqlStatus(tableName);
            int result = 1;
            if (result == 0) {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(tableName + "更删操作异常！");
                }
            } else if (result == 1) {
                return;
            } else {
                throw new RuntimeException(tableName + "更删操作异常！");

            }
        }
    }

}

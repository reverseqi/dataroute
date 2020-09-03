package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.datasource.DataSourceFactory;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.parsing.XNode;
import com.mingyi.dataroute.parsing.XPathParser;
import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;
import com.mingyi.dataroute.persistence.task.bsql.service.BSqlService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.jdbc.SqlRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlExecutor implements Executor {

    private static final Log log  = LogFactory.getLog(BSqlExecutor.class);

    @Override
    public Long execute(Integer id) {
        log.info("------------ 【"+ id +"】 BSQL任务开始 --------------");
        BSqlPO bSqlPO = SpringHelp.getBean(BSqlService.class).findById(id);
        DataSource dataSource = DataSourceFactory.getDataSource(String.valueOf(bSqlPO.getDatasourceId()));
        XNode xNode = XPathParser.evaluate(bSqlPO.getBsqlPath(), "/bsql");
        SqlRunner sqlRunner = null;
        try {
            sqlRunner = new SqlRunner(dataSource.getConnection());
            for (XNode child : xNode.getChildren()) {
                sqlRunner.run(child.getBody());
                log.info("--------------执行sql------------");
                log.info(child.getBody());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }

        log.info("------------ 【"+ id +"】 BSQL任务处理结束 --------------");
        return 1L;
    }
}

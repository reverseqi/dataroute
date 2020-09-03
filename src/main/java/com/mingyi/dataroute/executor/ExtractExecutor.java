package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.datasource.DataSourceFactory;
import com.mingyi.dataroute.datasource.Dialect;
import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;
import com.mingyi.dataroute.persistence.resource.datasource.service.DataSourceService;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽取执行器
 *
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractExecutor implements Executor {

    private static final Log log = LogFactory.getLog(ExtractExecutor.class);

    private DataSource originDataSource;
    private DataSourcePO originDataSourcePO;
    private Dialect originDialect;
    private DataSource targetDataSource;
    private ExtractPO extractPO;
    private Long extractAmount = 0L;
    private Integer id;



    @Override
    public Long execute(Integer id) {
        this.id = id;
        log.info("------------ 【"+ id +"】 抽取任务开始 --------------");
        ExtractService extractService = SpringHelp.getBean(ExtractService.class);
        DataSourceService dataSourceService = SpringHelp.getBean(DataSourceService.class);

        extractPO = extractService.findById(this.id);
        originDataSource = DataSourceFactory.getDataSource(String.valueOf(extractPO.getOriginDatasource()));
        targetDataSource = DataSourceFactory.getDataSource(String.valueOf(extractPO.getTargetDatasource()));
        originDataSourcePO = dataSourceService.findById(extractPO.getOriginDatasource());
        originDialect = Dialect.getDBDialect(originDataSourcePO.getType());

        // 清空目标表
        try {
            new SqlRunner(targetDataSource.getConnection()).run("truncate table "+extractPO.getTargetTable());
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return 1L;
        }

        List<Map<String, Object>> dataList;
        String lastTriggerMaxValue = extractPO.getLastTriggerMaxValue();
        while (true){
            dataList = extractOrigin(lastTriggerMaxValue);
            if (dataList != null && !dataList.isEmpty()) {
                lastTriggerMaxValue = dataList.get(dataList.size() - 1).get(extractPO.getTriggerField().trim().toUpperCase()).toString().substring(0, 19);
                insertTarget(dataList);
            }
            if (dataList == null || dataList.size() < extractPO.getBufferSize())
                break;
        }
        extractService.updateLastTriggerMaxValue(this.id, lastTriggerMaxValue);
        log.info("------------ 【"+ id +"】 抽取任务结束, 此次共抽取 "+ extractAmount +" --------------");
        return extractAmount;
    }


    /**
     * 抽取原始数据
     *
     * @param lastTriggerMaxValue 上次同步触发值
     * @return 获取数据
     */
    private List<Map<String, Object>> extractOrigin(String lastTriggerMaxValue){

        String extractSql = new SQL() {{
            SELECT(extractPO.getFields());
            FROM(extractPO.getOriginTable());
            WHERE(extractPO.getTriggerField() + " >= " + originDialect.formatTriggerCond());
            ORDER_BY(extractPO.getTriggerField());
        }}.toString();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        sb.append(extractPO.getFields());
        sb.append(" FROM ( ");
        sb.append(extractSql);
        sb.append(") WHERE rownum <= ");
        sb.append(extractPO.getBufferSize());
        extractSql = sb.toString();

        try {
            SqlRunner originRunner = new SqlRunner(originDataSource.getConnection());
            return originRunner.selectAll(extractSql, lastTriggerMaxValue);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 插入目标库
     *
     * @param dataList 源数据
     * @return 返回处理数量
     */
    private int insertTarget(final List<Map<String, Object>> dataList){

        // 导入
        String insertSql = new SQL(){{
            INSERT_INTO(extractPO.getTargetTable());
            INTO_COLUMNS(extractPO.getFields());
        }}.toString();

        List<Object> argList = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        for (Map<String, Object> map : dataList) {
            sb.append(", (");
            StringBuffer lineSb = new StringBuffer();
            for (String s : extractPO.getFields().split(",")) {
                Object object = map.get(s.toUpperCase().trim());
                argList.add(object == null ? "" : String.valueOf(object));
                lineSb.append(", ?");
            }
            sb.append(lineSb.substring(1));
            sb.append(")");
        };
        insertSql += (" VALUES " + sb.substring(1));
        int insert = 0;
        try {
            SqlRunner sqlRunner = new SqlRunner(targetDataSource.getConnection());
            insert = sqlRunner.insert(insertSql, argList.toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        extractAmount += dataList.size();

        log.info("------------ 【"+ id +"】 已抽取 "+ extractAmount +" --------------");
        return dataList.size();
    }

}

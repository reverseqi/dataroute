package com.mingyi.dataroute.db.datasource;

import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;
import com.mingyi.dataroute.persistence.resource.datasource.service.DataSourceService;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源工具类
 *
 * @author vbrug
 * @since 1.0.0
 */
public class DataSourcePool {

    private final ConcurrentHashMap<Integer, JobDataSource> dsCache = new ConcurrentHashMap<>();

    /**
     * 根据Id获取数据源对象
     *
     * @param dataSourceId 数据源Id
     * @return 数据源连接
     */
    public JobDataSource getDataSource(Integer dataSourceId) {
        if (dsCache.containsKey(dataSourceId))
            return dsCache.get(dataSourceId);
        JobDataSource dataSource = createDataSource(dataSourceId);
        dsCache.put(dataSourceId, dataSource);
        return dataSource;
    }


    /**
     * 创建数据源
     *
     * @param dataSourceId 数据源ID
     * @return 数据源对象
     */
    private JobDataSource createDataSource(Integer dataSourceId) {
        DataSourcePO dsPO = SpringHelp.getBean(DataSourceService.class).findById(dataSourceId);
        JobDataSource jobDataSource = null;
        if (StringUtils.hasText(dsPO.getUsername()) && StringUtils.hasText(dsPO.getPassword()))
            jobDataSource = new JobDataSource(dsPO.getDriver(), dsPO.getJdbcUrl(), dsPO.getUsername(), dsPO.getPassword());
        else
            jobDataSource = new JobDataSource(dsPO.getDriver(), dsPO.getJdbcUrl(), null);
        jobDataSource.setPoolMaximumActiveConnections(30);
        jobDataSource.setPoolMaximumCheckoutTime(60);
        jobDataSource.setPoolPingEnabled(true);
        jobDataSource.setPoolTimeToWait(600000);
        switch (Objects.requireNonNull(jobDataSource.getDialect().getDialectType())) {
            case ORACLE:
                jobDataSource.setPoolPingQuery("select * from dual");
                break;
            case MYSQL:
            case CLICKHOUSE:
                jobDataSource.setPoolPingQuery("select 1");
                break;
            case SQLSERVER:
            default:
        }
        return jobDataSource;
    }
}

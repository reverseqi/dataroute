package com.mingyi.dataroute.datasource;

import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;
import com.mingyi.dataroute.persistence.resource.datasource.service.DataSourceService;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.apache.ibatis.datasource.pooled.PooledDataSource;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源工具类
 *
 * @author vbrug
 * @since 1.0.0
 */
public class DataSourceFactory {

    private static final ConcurrentHashMap<String, DataSource> dsCache =
            new ConcurrentHashMap<>();

    /**
     * 根据Id获取数据源对象
     *
     * @param dataSourceId 数据源Id
     * @return 数据源连接
     */
    public static DataSource getDataSource(String dataSourceId) {
        if (dsCache.containsKey(dataSourceId))
            return dsCache.get(dataSourceId);
        DataSource dataSource = createDataSource(dataSourceId);
        dsCache.put(dataSourceId, dataSource);
        return dataSource;
    }

    /**
     * 创建数据源
     *
     * @param dataSourceId 数据源ID
     * @return 数据源对象
     */
    private static DataSource createDataSource(String dataSourceId) {
        DataSourcePO dsPO = SpringHelp.getBean(DataSourceService.class).findById(Integer.parseInt(dataSourceId));
        Dialect dialect = Dialect.getDBDialect(dsPO.getType());
        if (StringUtils.hasText(dsPO.getUsername()) && StringUtils.hasText(dsPO.getPassword()))
            return new PooledDataSource(dialect.jdbcDriver(), dsPO.getJdbcUrl(), dsPO.getUsername(), dsPO.getPassword());
        return new PooledDataSource(dialect.jdbcDriver(), dsPO.getJdbcUrl(), null);
    }


}

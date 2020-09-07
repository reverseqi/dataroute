package com.mingyi.dataroute.persistence.resource.datasource.service;

import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;

/**
 * 数据源信息查询接口
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface DataSourceService {

    /**
     * 根据id查询数据源配置信息
     *
     * @param id 数据源Id
     * @return 数据源信息
     */
    DataSourcePO findById(int id);

}

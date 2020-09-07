package com.mingyi.dataroute.persistence.resource.datasource.mapper;

import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface DataSourceMapper {

    /**
     * 根据ID查询数据源信息
     *
     * @param id 数据源ID
     * @return 数据源实体
     */
    @Select("select * from d_resource_datasource where id = #{id}")
    DataSourcePO findById(int id);
}

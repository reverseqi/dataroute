package com.mingyi.dataroute.persistence.resource.datasource.mapper;

import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface DataSourceMapper {

    @Select("select * from d_resource_datasource where id = #{id}")
    DataSourcePO findById(int id);

    @Select("select * from d_resource_datasource where id = #{id}")
    Map<String, Object> findById2(int id);
}

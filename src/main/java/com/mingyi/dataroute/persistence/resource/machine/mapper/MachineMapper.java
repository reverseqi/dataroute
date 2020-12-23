package com.mingyi.dataroute.persistence.resource.machine.mapper;

import com.mingyi.dataroute.persistence.resource.machine.po.MachinePO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface MachineMapper {

    /**
     * 根据主键查询服务器信息
     */
    @Select("select * from d_resource_machine where id = #{" +
            "id}")
    MachinePO findById(Integer id);

}

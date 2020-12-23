package com.mingyi.dataroute.persistence.system.config.mapper;

import com.mingyi.dataroute.persistence.system.config.po.ConfigPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface ConfigMapper {

    /**
     * 根据从的查询参数值
     */
    @Select("select * from pf_config where param_code = #{0}")
    ConfigPO findById(String paramCode);

}

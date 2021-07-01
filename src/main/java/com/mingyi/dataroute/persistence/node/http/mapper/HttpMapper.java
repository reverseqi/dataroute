package com.mingyi.dataroute.persistence.node.http.mapper;

import com.mingyi.dataroute.persistence.node.http.po.HttpPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface HttpMapper {

    /**
     * 根据ID查询任务实体
     */
    @Select("select * from d_node_http where id = #{0}")
    HttpPO findById(Integer nodeId);

}

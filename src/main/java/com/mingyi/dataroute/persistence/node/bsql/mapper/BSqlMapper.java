package com.mingyi.dataroute.persistence.node.bsql.mapper;

import com.mingyi.dataroute.persistence.node.bsql.entity.BSqlPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface BSqlMapper {

    @Select("select * from d_node_bsql where id = #{0}")
    BSqlPO findById(Integer nodeId);
}

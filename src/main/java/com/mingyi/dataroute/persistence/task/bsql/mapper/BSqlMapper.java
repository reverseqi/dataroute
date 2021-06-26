package com.mingyi.dataroute.persistence.task.bsql.mapper;

import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface BSqlMapper {

    @Select("select * from d_node_bsql where id = #{0}")
    BSqlPO findById(Integer nodeId);

    @Select("select id from d_node_bsql")
    List<Integer> selectAll();

}

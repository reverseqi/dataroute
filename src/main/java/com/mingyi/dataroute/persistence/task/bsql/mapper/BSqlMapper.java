package com.mingyi.dataroute.persistence.task.bsql.mapper;

import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface BSqlMapper {

    @Select("select * from d_task_bsql where process_id = #{processId} and node_id = #{nodeId}")
    BSqlPO findById(Integer processId, Integer nodeId);

    @Select("select node_id from d_task_bsql")
    List<Integer> selectAll();

}

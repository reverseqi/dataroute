package com.mingyi.dataroute.persistence.task.http.mapper;

import com.mingyi.dataroute.persistence.task.http.po.HttpPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface HttpMapper {

    /**
     * 根据ID查询任务实体
     */
    @Select("select * from d_task_http where process_id = #{processId} and node_id = #{nodeId}")
    HttpPO findById(Integer processId, Integer nodeId);

}

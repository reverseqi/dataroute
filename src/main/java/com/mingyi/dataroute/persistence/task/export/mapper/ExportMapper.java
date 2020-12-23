package com.mingyi.dataroute.persistence.task.export.mapper;

import com.mingyi.dataroute.persistence.task.export.po.ExportPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface ExportMapper {

    @Select("select * from d_task_export where node_id = #{nodeId} and process_id = #{processId}")
    ExportPO findById(Integer processId, Integer nodeId);

}

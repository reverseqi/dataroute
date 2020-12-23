package com.mingyi.dataroute.persistence.task.vimport.mapper;

import com.mingyi.dataroute.persistence.task.vimport.po.ImportPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */

public interface ImportMapper {

    @Select("select * from d_task_import where process_id = #{processId} and node_id = #{nodeId}")
    ImportPO findById(Integer processId, Integer nodeId);
}

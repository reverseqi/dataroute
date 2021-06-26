package com.mingyi.dataroute.persistence.task.fileud.mapper;

import com.mingyi.dataroute.persistence.task.fileud.po.FileUDPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface FileUDMapper {

    /**
     * 主键查询任务实体
     */
    @Select("select * from d_node_file_ud where process_id = #{processId} and node_id = #{nodeId}")
    FileUDPO findById(Integer processId, Integer nodeId);


}

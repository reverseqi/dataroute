package com.mingyi.dataroute.persistence.node.fileud.mapper;

import com.mingyi.dataroute.persistence.node.fileud.po.FileUDPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface FileUDMapper {

    /**
     * 主键查询任务实体
     */
    @Select("select * from d_node_file_ud where id = #{0}")
    FileUDPO findById(Integer nodeId);


}

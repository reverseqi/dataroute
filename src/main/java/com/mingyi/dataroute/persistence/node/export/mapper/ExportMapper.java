package com.mingyi.dataroute.persistence.node.export.mapper;

import com.mingyi.dataroute.persistence.node.export.po.ExportPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface ExportMapper {

    @Select("select * from d_node_export where node_id = #{0}")
    ExportPO findById(Integer nodeId);

}

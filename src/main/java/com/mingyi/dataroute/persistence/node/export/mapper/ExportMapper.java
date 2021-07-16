package com.mingyi.dataroute.persistence.node.export.mapper;

import com.mingyi.dataroute.persistence.node.export.entity.ExportPO;
import org.apache.ibatis.annotations.Select;

/**
 * 导出节点Mapper
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface ExportMapper {

    @Select("select * from d_node_export where id = #{0}")
    ExportPO findById(Integer nodeId);

}

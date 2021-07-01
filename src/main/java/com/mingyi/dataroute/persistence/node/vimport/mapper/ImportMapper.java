package com.mingyi.dataroute.persistence.node.vimport.mapper;

import com.mingyi.dataroute.persistence.node.vimport.po.ImportPO;
import org.apache.ibatis.annotations.Select;

/**
 * @author vbrug
 * @since 1.0.0
 */

public interface ImportMapper {

    @Select("select * from d_node_import where id = #{0}")
    ImportPO findById(Integer nodeId);
}

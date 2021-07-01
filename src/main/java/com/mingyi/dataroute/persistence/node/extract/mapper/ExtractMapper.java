package com.mingyi.dataroute.persistence.node.extract.mapper;

import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 数据抽取Mapper
 * @author vbrug
 * @since 1.0.0
 */
public interface ExtractMapper {

    @Select("select * from d_node_extract where id = #{0}")
    ExtractPO findById(Integer nodeId);

    @Update("update d_node_extract set extract_cond_field = #{triggerValue} where id = #{id}")
    int updateTriggerValue(Integer id, String triggerValue);
}

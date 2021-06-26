package com.mingyi.dataroute.persistence.task.extract.mapper;

import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 数据抽取Mapper
 * @author vbrug
 * @since 1.0.0
 */
public interface ExtractMapper {

    @Select("select * from d_node_extract where id = #{nodeId}")
    ExtractPO findById(Integer nodeId);

    @Select("select node_id from d_node_extract")
    List<Integer> selectAllTask();

    @Update("update d_node_extract set extract_cond_field = #{triggerValue} where id = #{id}")
    int updateTriggerValue(Integer id, String triggerValue);
}

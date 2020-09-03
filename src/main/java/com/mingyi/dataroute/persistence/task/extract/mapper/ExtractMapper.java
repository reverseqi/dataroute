package com.mingyi.dataroute.persistence.task.extract.mapper;

import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 数据抽取Mapper
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface ExtractMapper {

    @Select("select * from d_task_extract where node_id = #{nodeId}")
    ExtractPO findById(Integer nodeId);

    @Select("select node_id from d_task_extract")
    List<Integer> selectAllTask();

    @Update("update d_task_extract set last_trigger_max_value = #{lastTriggerMaxValue} where node_id = #{nodeId}")
    int updateLastTriggerMaxValue(Integer nodeId, String lastTriggerMaxValue);
}

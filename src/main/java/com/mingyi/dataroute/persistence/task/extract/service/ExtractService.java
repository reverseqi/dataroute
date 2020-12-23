package com.mingyi.dataroute.persistence.task.extract.service;

import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;

import java.util.List;

/**
 * 数据抽取
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface ExtractService {

    /**
     * 根据主键查询抽取任务详情
     *
     * @param processId 流程编号
     * @param nodeId 节点编号
     * @return 持久化对象
     */
    ExtractPO findById(Integer processId, Integer nodeId);

    /**
     * 查询所有节点信息
     * @return
     */
    List<Integer> selectAllTask();

    int updateTriggerValue(Integer processId, Integer nodeId, String triggerValue);


}

package com.mingyi.dataroute.persistence.node.extract.service;

import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;

import java.util.List;

/**
 * 数据抽取
 * @author vbrug
 * @since 1.0.0
 */
public interface ExtractService {

    /**
     * 根据主键查询抽取任务详情
     * @param nodeId 节点编号
     * @return 持久化对象
     */
    ExtractPO findById(Integer nodeId);

    /**
     * 查询所有节点信息
     * @return
     */
    List<Integer> selectAllTask();

    int updateTriggerValue(Integer nodeId, String triggerValue);


}

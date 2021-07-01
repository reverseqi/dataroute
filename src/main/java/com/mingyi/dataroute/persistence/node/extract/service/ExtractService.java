package com.mingyi.dataroute.persistence.node.extract.service;

import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;

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
     * 更新触发条件
     * @return 结果
     */
    int updateTriggerValue(Integer nodeId, String triggerValue);


}

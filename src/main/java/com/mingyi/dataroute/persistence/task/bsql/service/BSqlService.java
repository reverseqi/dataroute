package com.mingyi.dataroute.persistence.task.bsql.service;

import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;

import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface BSqlService {


    /**
     * 查询抽取任务
     *
     * @param nodeId 节点ID
     * @return 任务实体
     */
    BSqlPO findById(Integer processId, Integer nodeId);

    List<Integer> selectAll();
}

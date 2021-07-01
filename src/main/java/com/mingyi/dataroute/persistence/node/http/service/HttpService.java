package com.mingyi.dataroute.persistence.node.http.service;

import com.mingyi.dataroute.persistence.node.http.po.HttpPO;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface HttpService {

    /**
     * 根据主键查询
     * @param nodeId 节点ID
     * @return 任务实体
     */
    HttpPO findById(Integer nodeId);

}

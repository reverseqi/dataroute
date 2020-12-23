package com.mingyi.dataroute.persistence.task.fileud.service;

import com.mingyi.dataroute.persistence.task.fileud.po.FileUDPO;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface FileUDService {


    /**
     * 主键查询任务实体
     */
    FileUDPO findById(Integer processId, Integer nodeId);

}

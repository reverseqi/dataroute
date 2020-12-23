package com.mingyi.dataroute.persistence.resource.machine.service;

import com.mingyi.dataroute.persistence.resource.machine.po.MachinePO;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface MachineService {

    /**
     * 根据主键查询服务器信息
     */
    MachinePO findById(Integer id);
}

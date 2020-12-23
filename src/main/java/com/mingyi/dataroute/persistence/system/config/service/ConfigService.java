package com.mingyi.dataroute.persistence.system.config.service;

import com.mingyi.dataroute.persistence.system.config.po.ConfigPO;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface ConfigService {

    /**
     * 根据主键查询参数值
     */
    ConfigPO findById(String paramCode);

}

package com.mingyi.dataroute.persistence.system.config.service.impl;

import com.mingyi.dataroute.persistence.system.config.mapper.ConfigMapper;
import com.mingyi.dataroute.persistence.system.config.po.ConfigPO;
import com.mingyi.dataroute.persistence.system.config.service.ConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigMapper mapper;


    @Override
    public ConfigPO findById(String paramCode) {
        return mapper.findById(paramCode);
    }
}

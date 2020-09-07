package com.mingyi.dataroute.persistence.resource.datasource.service.impl;

import com.mingyi.dataroute.persistence.resource.datasource.mapper.DataSourceMapper;
import com.mingyi.dataroute.persistence.resource.datasource.po.DataSourcePO;
import com.mingyi.dataroute.persistence.resource.datasource.service.DataSourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Resource
    private DataSourceMapper mapper;

    @Override
    public DataSourcePO findById(int id) {
        return mapper.findById(id);
    }
}

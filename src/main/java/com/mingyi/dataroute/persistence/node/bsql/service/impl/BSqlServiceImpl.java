package com.mingyi.dataroute.persistence.node.bsql.service.impl;

import com.mingyi.dataroute.persistence.node.bsql.entity.BSqlPO;
import com.mingyi.dataroute.persistence.node.bsql.mapper.BSqlMapper;
import com.mingyi.dataroute.persistence.node.bsql.service.BSqlService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class BSqlServiceImpl implements BSqlService {

    @Resource
    private BSqlMapper mapper;

    @Override
    public BSqlPO findById(Integer nodeId) {
        return mapper.findById(nodeId);
    }
}

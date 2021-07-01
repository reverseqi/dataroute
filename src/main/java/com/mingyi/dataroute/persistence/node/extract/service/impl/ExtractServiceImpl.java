package com.mingyi.dataroute.persistence.node.extract.service.impl;

import com.mingyi.dataroute.persistence.node.extract.mapper.ExtractMapper;
import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.node.extract.service.ExtractService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 抽取Service
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class ExtractServiceImpl implements ExtractService {

    @Resource
    private ExtractMapper mapper;

    @Override
    public ExtractPO findById(Integer nodeId) {
        return mapper.findById(nodeId);
    }

    @Override
    public int updateTriggerValue(Integer nodeId, String triggerValue) {
        return mapper.updateTriggerValue(nodeId, triggerValue);
    }
}

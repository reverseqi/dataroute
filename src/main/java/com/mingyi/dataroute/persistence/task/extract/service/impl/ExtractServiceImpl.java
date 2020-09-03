package com.mingyi.dataroute.persistence.task.extract.service.impl;

import com.mingyi.dataroute.persistence.task.extract.mapper.ExtractMapper;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
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
    public int updateLastTriggerMaxValue(Integer nodeId, String lastTriggerMaxValue) {
        return mapper.updateLastTriggerMaxValue(nodeId, lastTriggerMaxValue);
    }

    @Override
    public List<Integer> selectAllTask() {
        return mapper.selectAllTask();
    }
}

package com.mingyi.dataroute.persistence.task.bsql.service.impl;

import com.mingyi.dataroute.persistence.task.bsql.mapper.BSqlMapper;
import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;
import com.mingyi.dataroute.persistence.task.bsql.service.BSqlService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class BSqlServiceImpl implements BSqlService {

    @Resource
    private BSqlMapper mapper;

    @Override
    public BSqlPO findById(Integer processId, Integer nodeId) {
        return mapper.findById(processId, nodeId);
    }

    @Override
    public List<Integer> selectAll() {
        return mapper.selectAll();
    }
}

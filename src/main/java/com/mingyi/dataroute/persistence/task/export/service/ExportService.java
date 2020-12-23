package com.mingyi.dataroute.persistence.task.export.service;

import com.mingyi.dataroute.persistence.task.export.mapper.ExportMapper;
import com.mingyi.dataroute.persistence.task.export.po.ExportPO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class ExportService {

    @Resource
    private ExportMapper mapper;

    public ExportPO findById(Integer processId, Integer nodeId){
        return mapper.findById(processId, nodeId);
    }

}

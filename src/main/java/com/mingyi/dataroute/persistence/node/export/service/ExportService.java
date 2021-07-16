package com.mingyi.dataroute.persistence.node.export.service;

import com.mingyi.dataroute.persistence.node.export.mapper.ExportMapper;
import com.mingyi.dataroute.persistence.node.export.entity.ExportPO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 导出节点Service
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class ExportService {

    @Resource
    private ExportMapper mapper;

    public ExportPO findById(Integer nodeId){
        return mapper.findById(nodeId);
    }

}

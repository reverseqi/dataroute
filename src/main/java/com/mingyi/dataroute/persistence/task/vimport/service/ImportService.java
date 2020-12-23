package com.mingyi.dataroute.persistence.task.vimport.service;

import com.mingyi.dataroute.persistence.task.vimport.mapper.ImportMapper;
import com.mingyi.dataroute.persistence.task.vimport.po.ImportPO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class ImportService {

    @Resource
    private ImportMapper mapper;

    public ImportPO findById(Integer processId, Integer nodeId) {
        return mapper.findById(processId, nodeId);
    }

}

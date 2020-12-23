package com.mingyi.dataroute.persistence.task.fileud.service.impl;

import com.mingyi.dataroute.persistence.task.fileud.mapper.FileUDMapper;
import com.mingyi.dataroute.persistence.task.fileud.po.FileUDPO;
import com.mingyi.dataroute.persistence.task.fileud.service.FileUDService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class FileUDServiceImpl implements FileUDService {

    @Resource
    private FileUDMapper mapper;


    @Override
    public FileUDPO findById(Integer processId, Integer nodeId) {
        return mapper.findById(processId, nodeId);
    }
}

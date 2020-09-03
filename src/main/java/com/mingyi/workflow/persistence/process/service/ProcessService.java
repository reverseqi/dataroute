package com.mingyi.workflow.persistence.process.service;

import com.mingyi.workflow.persistence.process.mapper.ProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProcessService {

    @Autowired
    private ProcessMapper mapper;

    public List<Map<String, Object>> selectList() {
        return mapper.selectList();
    }

}

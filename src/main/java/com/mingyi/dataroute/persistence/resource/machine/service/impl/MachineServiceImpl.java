package com.mingyi.dataroute.persistence.resource.machine.service.impl;

import com.mingyi.dataroute.persistence.resource.machine.mapper.MachineMapper;
import com.mingyi.dataroute.persistence.resource.machine.po.MachinePO;
import com.mingyi.dataroute.persistence.resource.machine.service.MachineService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class MachineServiceImpl implements MachineService {

    @Resource
    private MachineMapper mapper;

    @Override
    public MachinePO findById(Integer id) {
        return mapper.findById(id);
    }
}

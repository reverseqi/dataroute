package com.mingyi.dataroute.persistence.node.http.service.impl;

import com.mingyi.dataroute.persistence.node.http.mapper.HttpMapper;
import com.mingyi.dataroute.persistence.node.http.po.HttpPO;
import com.mingyi.dataroute.persistence.node.http.service.HttpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * http service 实现类
 *
 * @author vbrug
 * @since 1.0.0
 */
@Service
public class HttpServiceImpl implements HttpService {

    @Resource
    private HttpMapper mapper;

    @Override
    public HttpPO findById(Integer processId, Integer nodeId) {
        return mapper.findById(processId, nodeId);
    }
}

package com.mingyi.dataroute.persistence.task.bsql.service;

import com.mingyi.dataroute.persistence.task.bsql.po.BSqlPO;

import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface BSqlService {


    BSqlPO findById(Integer nodeId);

    List<Integer> selectAll();
}

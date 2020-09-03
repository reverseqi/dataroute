package com.mingyi.dataroute.persistence.task.extract.service;

import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;

import java.util.List;

/**
 * 数据抽取
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface ExtractService {

    ExtractPO findById(Integer nodeId);


    int updateLastTriggerMaxValue(Integer nodeId, String lastTriggerMaxValue);


    List<Integer> selectAllTask();


}

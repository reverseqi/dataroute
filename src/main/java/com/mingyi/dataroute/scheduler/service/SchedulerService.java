package com.mingyi.dataroute.scheduler.service;


import com.mingyi.dataroute.persistence.scheduler.entity.SchedulerPO;

import java.util.List;

public interface SchedulerService {

    /**
     * 重启所有的定时任务
     * @return
     */
    String initAllProcess();

    /**
     * 轮巡启动所有任务
     * @return
     */
    String initLoopProcess();

    /**
     * 启动查询所有的定时任务
     * @return
     */
    //String  initAllProcess();


    /**
     * 查询指定 定时任务
     * @return
     */
    String addJob(Integer id);

    /**
     * 删除指定 定时任务
     * @return
     */

    String deleteJob(Integer id);


    /**
     * 查看执行状态
     * @return
     */
    String viewStatByJob();

    /**
     * 暂停调度
     * @return
     */
    String standByScheduler();



    /**
     * 启动调度
     * @return
     */
    String startScheduler();


    /**
     * 更新失败状态
     * @param jobId
     * @return
     */
    String updateActFailState(Long jobId);


    /**
     * 更新成功状态
     * @param jobId
     * @return
     */
    String updateActSuccessState(Long jobId);

    /**
     * 查询活动中的任务
     * @return
     */
    List<SchedulerPO> findProcessAllAct();

    /**
     * 查询活动的任务根据JOBID
     * @param jobId
     * @return
     */
    SchedulerPO findProcessActByJobId(Long jobId);

    SchedulerPO findProcessById(Integer processId);

    List<SchedulerPO> findProcessActById(Integer processId);
}

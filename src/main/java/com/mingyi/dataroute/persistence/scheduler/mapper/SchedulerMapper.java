package com.mingyi.dataroute.persistence.scheduler.mapper;

import com.mingyi.dataroute.persistence.scheduler.entity.SchedulerPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author vbrug
 * @since 1.0.0
 */
public interface SchedulerMapper {

    @Select("select * from d_def_scheduler where stop_flag = 0 ")
    List<SchedulerPO> findAllProcess();

    @Select("select * from d_def_scheduler where stop_flag = 1 ")
    List<SchedulerPO> findStopProcess();

    @Select("select * from d_def_scheduler where process_id = #{processId} ")
    SchedulerPO findProcessById(@Param("processId") Integer processId);

    /**
     * 查询正在活动的任务
     * @param processId
     * @return
     */
    @Select("select * from d_act_scheduler where process_id = #{processId} order by create_time desc ")
    List<SchedulerPO> findProcessActById(Integer processId);

    /**
     * 更新成功状态
     * @param jobId
     * @return
     */
    @Update("update d_act_scheduler set state = 9 where job_id = #{jobId} ")
    String updateActFailState(Long jobId);

    /**
     * 更新成功状态
     * @param jobId
     * @return
     */
    @Update("update d_act_scheduler set state = 2 where job_id = #{jobId} ")
    String updateActSuccessState(Long jobId);

    /**
     * 迁移到历史表中
     * @param jobId
     * @return
     */
    @Update("update d_act_scheduler set state = 2 where job_id = #{jobId} ")
    String actToHis(Long jobId);

    /**
     * 更新成功状态
     * @param jobId
     * @return
     */
    @Update("delete d_act_scheduler  where job_id = #{jobId} ")
    String deleteAct(Long jobId);

    /**
     * 查询所有待执行的任务
     * @return
     */
    @Select("select * from d_act_scheduler order by create_time desc ")
    List<SchedulerPO> findProcessAllAct();

    /**
     * 查询当前任务中的JOB
     * @param jobId
     * @return
     */
    SchedulerPO findProcessActByJobId(Long jobId);
}

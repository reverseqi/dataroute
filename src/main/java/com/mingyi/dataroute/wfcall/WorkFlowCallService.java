package com.mingyi.dataroute.wfcall;

import com.mingyi.dataroute.executor.ExecutorFactory;
import com.vbrug.fw4j.core.entity.Result;
import com.vbrug.workflow.core.WorkFlowEngine;
import com.vbrug.workflow.core.constants.WFConstants;
import com.vbrug.workflow.core.context.JobContext;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.WFResultCode;
import com.vbrug.workflow.core.exceptions.WorkFlowException;
import com.vbrug.workflow.core.persistence.instance.task.entity.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * 工作流调用
 * @author vbrug
 * @since 1.0.0
 */
public class WorkFlowCallService {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowCallService.class);

    private static final WorkFlowEngine flowEngine = WorkFlowEngine.getInstance();

    public static int FAIL_TEST = 0;


    /**
     * 流程启动
     * @param processId 流程ID
     */
    public static void startProcess(Integer processId) {
        // 启动流程
        JobContext jobContext = flowEngine.newJob(processId, null).getData(JobContext.class);
        // 创建作业环境
        logger.info("------------ -V- -V- -V- 作业：【{}--{}】 执行开始 -V- -V- -V- ------------", jobContext.getJobId(), jobContext.getJobName());
        Result result = flowEngine.gTodoTasks(jobContext.getJobId());
        execTask(taskContext, result.getData2List(TaskDTO.class));
    }

    /**
     * 获取待执行的任务
     */
    public static void getNextTask(TaskContext taskContext) {
        // 获取下一待执行任务
        Result result1 = flowEngine.gTodoTasks(taskContext.getTaskId(), WFConstants.TASK_PRECONDITION_YES);
        if (result1.getStatus() == 1) {
            if (result1.getBCode().equals(WFResultCode.FINISH.getBCode())) {
                logger.info("------------ -V- -V- -V- 作业：【{}--{}】 结束 -V- -V- -V- ------------", taskContext.getJobContext().getJobId(), taskContext.getJobContext().getJobName());
            } else if (result1.getBCode().equals(WFResultCode.NO_TODO_TASK.getBCode())) {
                logger.info("当前任务{}-{}，没有后续执行节点", );
            } else {
                execTask(taskContext, result1.getData2List(TaskDTO.class));
            }
        } else {
            throw new WorkFlowException("流程异常，状态码{} 详细信息 {}", result1.getBCode(), result1.getBMessage());
        }
    }

    /**
     * 执行任务
     */
    public static void execTask(JobContext jobContext, List<TaskDTO> taskDTOList) {
        for (TaskDTO taskDTO : taskDTOList) {
            TaskContext taskContext = jobContext.getTaskContext(taskDTO.getId());
            new Thread(() -> {
                logger.info("【{}-{}】，开始执行", taskContext.getTaskId(), taskContext.getTaskName());
                StopWatch taskElapsedTimeWatch = new StopWatch();
                taskElapsedTimeWatch.start();
                try {
                    if (FAIL_TEST == 0)
                        ExecutorFactory.createExecutor(taskContext).execute();
                    flowEngine.completeTask(taskContext.getTaskId(), null);
                } catch (Exception e) {
                    flowEngine.recordFailTask(taskContext.getTaskId(), e.getMessage());
                    taskElapsedTimeWatch.stop();
                    logger.error("【{}-{}】 任务耗时：{}, 执行失败。{}", taskContext.getTaskId(), taskContext.getTaskName(), taskElapsedTimeWatch.getTotalTimeSeconds(), e);
                    return;
                }
                taskElapsedTimeWatch.stop();
                logger.info("【{}-{}】，结束，任务耗时：{}", taskContext.getTaskId(), taskContext.getTaskName(), taskElapsedTimeWatch.getTotalTimeSeconds());
                if (!jobContext.isStop()) {
                    try {
                        WorkFlowCallService.getNextTask(currentTaskContext);
                    } catch (Exception e) {
                        logger.error("【{}-{}】 工作流调用发生异常。{}", taskContext.getTaskId(), taskContext.getTaskName(), taskElapsedTimeWatch.getTotalTimeSeconds(), e);
                        return;
                    }
                } else {
                    logger.info("------------ -V- -V- -V- 作业【{}-{}】停止，不再执行后续任务 -V- -V- -V- ------------", jobContext.getJobId(), jobContext.getJobName());
                }

            }).start();
        }
    }

    public static void execFailTask(Long id) {
        Result result = flowEngine.redoFailTasks(id);
        execTask(failTaskMonitorMap.get(id), result.getData2List(TaskDTO.class));
        failTaskMonitorMap.remove(id);
    }

    public static void stopProcess(JobContext jobContext) {
        jobContext.setStop(true);
        logger.info("------------ -V- -V- -V- 作业：【{}-{}】, 停止, 执行中任务仍继续执行 -V- -V- -V- ------------", jobContext.getJobId(), jobContext.getJobName());
    }

}


package com.mingyi.dataroute.wfcall;

import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.JobContextBuilder;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.ExecutorFactory;
import com.vbrug.fw4j.core.entity.Result;
import com.vbrug.workflow.core.WorkFlowEngine;
import com.vbrug.workflow.core.constants.WFConstants;
import com.vbrug.workflow.core.entity.WFResultCode;
import com.vbrug.workflow.core.exceptions.WorkFlowException;
import com.vbrug.workflow.core.persistence.instance.task.entity.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 工作流调用
 * @author vbrug
 * @since 1.0.0
 */
public class WorkFlowCallService {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowCallService.class);


    public static final ConcurrentMap<Long, JobContext> failJob = new ConcurrentHashMap<>();

    public static final ConcurrentMap<Long, TaskContext> failTaskMonitorMap = new ConcurrentHashMap<>();

    private static final WorkFlowEngine flowEngine = WorkFlowEngine.getInstance();

    public static int FAIL_TEST = 0;


    /**
     * 流程启动
     * @param processId 流程ID
     */
    public static void startProcess(Integer processId) {
        // 启动流程
        Long jobId = flowEngine.newJob(processId, null).getData(Long.class);
        // 创建作业环境
        JobContext jobContext = JobContextBuilder.newJobContext(jobId, "固定作业", processId).build();
        logger.info("------------ -V- -V- -V- 作业：【{}--{}】 执行开始 -V- -V- -V- ------------", jobId, jobContext.getJobName());
        Result      result      = flowEngine.gTodoTasks(jobId);
        TaskContext taskContext = new TaskContext.Builder(0L, 0, "开始", jobContext, "START").build();
        execTask(taskContext, result.getData2List(TaskDTO.class));
    }

    /**
     * 获取待执行的任务
     */
    public static void getNextTask(TaskContext taskContext) {
        flowEngine.completeTask(taskContext.getId(), null);
        // 获取下一待执行任务
        Result result1 = flowEngine.gTodoTasks(taskContext.getId(), WFConstants.TASK_PRECONDITION_YES);
        if (result1.getStatus() == 1) {
            if (result1.getBCode().equals(WFResultCode.FINISH.getBCode())) {
                logger.info("------------ -V- -V- -V- 作业：【{}--{}】 结束 -V- -V- -V- ------------", taskContext.getJobContext().getJobId(), taskContext.getJobContext().getJobName());
                if (failJob.containsKey(taskContext.getJobContext().getJobId())) {
                    failJob.remove(taskContext.getJobContext().getJobId());
                }
            } else if (result1.getBCode().equals(WFResultCode.NO_TODO_TASK.getBCode())) {
                System.out.println("没有待执行节点");
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
    public static void execTask(TaskContext lastTaskContext, List<TaskDTO> taskDTOList) {
        JobContext jobContext = lastTaskContext.getJobContext();

        for (TaskDTO taskDTO : taskDTOList) {
            new Thread(() -> {
                TaskContext currentTaskContext = new TaskContext.Builder(taskDTO.getId(), taskDTO.getNodeId(), taskDTO.getNodeName(), jobContext, taskDTO.getNodeType()).build();
                logger.info("【{}-{}】，开始执行", currentTaskContext.getId(), currentTaskContext.getNodeName());
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                jobContext.putTaskContext(currentTaskContext);
                try {
                    if (FAIL_TEST == 0)
                        ExecutorFactory.createExecutor(taskDTO.getNodeType(), currentTaskContext).execute();
                } catch (Exception e) {
                    flowEngine.recordFailTask(currentTaskContext.getId());
                    failTaskMonitorMap.put(currentTaskContext.getJobContext().getJobId(), lastTaskContext);
                    failJob.put(currentTaskContext.getJobContext().getJobId(), currentTaskContext.getJobContext());
                    stopWatch.stop();
                    logger.error("【{}-{}】 任务耗时：{}, 执行失败。{}", currentTaskContext.getId(), currentTaskContext.getNodeName(), stopWatch.getTotalTimeSeconds(), e);
                    return;
                }
                stopWatch.stop();
                logger.info("【{}-{}】，结束，任务耗时：{}", currentTaskContext.getId(), currentTaskContext.getNodeName(), stopWatch.getTotalTimeSeconds());
                if (!jobContext.isStop()) {
                    try {
                        WorkFlowCallService.getNextTask(currentTaskContext);
                    } catch (Exception e) {
                        logger.error("【{}-{}】 工作流调用发生异常。{}", currentTaskContext.getId(), currentTaskContext.getNodeName(), stopWatch.getTotalTimeSeconds(), e);
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


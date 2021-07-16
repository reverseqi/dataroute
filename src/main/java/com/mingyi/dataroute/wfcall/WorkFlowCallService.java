package com.mingyi.dataroute.wfcall;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.mingyi.dataroute.executor.ExecutorFactory;
import com.vbrug.fw4j.common.entity.Result;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.workflow.core.WorkFlowEngine;
import com.vbrug.workflow.core.context.JobContext;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import com.vbrug.workflow.core.entity.WFResultCode;
import com.vbrug.workflow.core.exceptions.WorkFlowException;
import com.vbrug.workflow.core.persistence.instance.task.entity.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Objects;

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
     * 新建作业
     * @param processId 流程ID
     * @return 作业ID
     */
    public static Long newJob(Integer processId) {
        JobContext jobContext = flowEngine.newJob(processId, null).getData(JobContext.class);
        return jobContext.getJobId();
    }

    /**
     * 执行作业
     * @param jobId 作业ID
     */
    public static void doJob(Long jobId) {
        JobContext jobContext = flowEngine.gJobContext(jobId).getData(JobContext.class);
        logger.info(">>>>>>>>>>>>>>>>>>>> 作业：{}--{} 执行开始 <<<<<<<<<<<<<<<<<<<<", jobContext.getJobId(), jobContext.getJobName());
        Result result = flowEngine.gTodoTasks(jobContext.getJobId());
        if (assertJobFinish(result, jobContext))
            return;
        if (result.getBCode().equals(WFResultCode.NO_TODO_TASK.getBCode())) {
            throw new DataRouteException(StringUtils.replacePlaceholder("当前作业 {} 无可执行任务", jobContext.getJobId()));
        }
        _execTask(jobContext, result.getData2List(TaskDTO.class));
    }

    /**
     * 重跑作业失败的任务
     * @param jobId 作业ID
     */
    public static void redoFailJob(Long jobId) {
        JobContext jobContext = flowEngine.gJobContext(jobId).getData(JobContext.class);
        logger.info(">>>>>>>>>>>>>>>>>>>> 作业：{}--{} 重跑失败任务 <<<<<<<<<<<<<<<<<<<<", jobContext.getJobId(), jobContext.getJobName());
        Result result = flowEngine.redoFailTasks(jobContext.getJobId());
        if (assertJobFinish(result, jobContext))
            return;
        if (result.getBCode().equals(WFResultCode.NO_TODO_TASK.getBCode())) {
            logger.info("当前作业 {} 无失败任务", jobContext.getJobId());
            return;
        }
        _execTask(jobContext, result.getData2List(TaskDTO.class));
    }

    /**
     * 停止作业
     * @param jobId 作业ID
     */
    public static void stopJob(Long jobId) {
        JobContext jobContext = flowEngine.gJobContext(jobId).getData(JobContext.class);
        logger.info(">>>>>>>>>>>>>>>>>>>> 作业：{}--{} 强制停止, 执行中任务仍继续执行 <<<<<<<<<<<<<<<<<<<<", jobContext.getJobId(), jobContext.getJobName());
    }

    /**
     * 判断执行结果, 1-成功，0-异常
     * @param result 结果对象
     * @return false-未完成, true-作业完成
     */
    private static boolean assertJobFinish(Result result, JobContext jobContext) {
        if (result.getStatus() == 1) {
            if (result.getBCode().equals(WFResultCode.FINISH.getBCode())) {
                logger.info(">>>>>>>>>>>>>>>>>>>> 作业：{}--{} 执行完成 <<<<<<<<<<<<<<<<<<<<", jobContext.getJobId(), jobContext.getJobName());
                return true;
            }
            return false;
        } else {
            throw new WorkFlowException("流程异常，状态码{} 详细信息 {}", result.getBCode(), result.getBMessage());
        }
    }

    /**
     * 执行任务
     */
    private static void _execTask(JobContext jobContext, List<TaskDTO> taskDTOList) {
        for (TaskDTO taskDTO : taskDTOList) {
            TaskContext taskContext = jobContext.getTaskContext(taskDTO.getId());
            new Thread(() -> {
                logger.info("【{}-{}】，开始执行", taskContext.getTaskId(), taskContext.getTaskName());
                StopWatch taskElapsedTimeWatch = new StopWatch();
                taskElapsedTimeWatch.start();
                try {
                    // 执行任务
                    TaskResult taskResult = null;
                    if (FAIL_TEST == 0) {
                        taskResult = Objects.requireNonNull(ExecutorFactory.createExecutor(taskContext)).execute();
                    }
                    flowEngine.completeTask(taskResult);
                    // 判断任务结果
                    Result result = flowEngine.gTodoTasks(taskContext.getTaskId(), TaskResult.PRECONDITION_YES);
                    if (assertJobFinish(result, jobContext))
                        return;
                    if (result.getBCode().equals(WFResultCode.NO_TODO_TASK.getBCode())) {
                        logger.info("当前{} ->> {} 无后续待执行任务", taskContext.getJobContext().getJobId(), taskContext.getTaskId());
                        return;
                    }
                    _execTask(jobContext, result.getData2List(TaskDTO.class));
                } catch (Exception e) {
                    flowEngine.recordFailTask(TaskResult.newInstance(taskContext.getTaskId()).setRemark("异常信息：" + e.getMessage()));
                    logger.error("【{}-{}】 任务耗时：{}, 执行失败。{}", taskContext.getTaskId(), taskContext.getTaskName(), taskElapsedTimeWatch.getTotalTimeSeconds(), e);
                    return;
                } finally {
                    taskElapsedTimeWatch.stop();
                }
                logger.info("【{}-{}】，结束，任务耗时：{}", taskContext.getTaskId(), taskContext.getTaskName(), taskElapsedTimeWatch.getTotalTimeSeconds());
            }).start();
        }
    }
}


package com.mingyi.dataroute.wfcall;

import com.mingyi.dataroute.constant.WFConstants;
import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.JobContextBuilder;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.ExecutorFactory;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 工作流调用
 *
 * @author vbrug
 * @since 1.0.0
 */
public class WorkFlowCallService2 {

/*
    private static final Logger logger = LoggerFactory.getLogger(WorkFlowCallService2.class);

    public static final ConcurrentMap<String, JobContext> processMonitorMap = new ConcurrentHashMap<>();

    public static final ConcurrentMap<String, TaskContext> failTaskMonitorMap = new ConcurrentHashMap<>();


    */
/**
     * 流程启动
     *
     * @param processId 流程ID
     *//*

    public static void startProcess(Integer processId) {
        ResultBean<List<NodeBean>> resultBean = WorkFlowEngine.getInstance().startJob(processId);
        Map<String, Object> result = resultBean.getResult();
        Integer jobId = ObjectUtils.castInteger(result.get(WFConstants.PARAM_JOB_ID));
        // 创建作业环境
        JobContext jobContext = JobContextBuilder.newJobContext(jobId, String.valueOf(result.get("jobName")), processId).build();
        processMonitorMap.put(String.valueOf(processId), jobContext);

        // TODO  数据库灵活配置
        jobContext.getDataMap().put("rootFilePath", "/root/dataroute");

        // 创建当前任务环境
        NodeBean nodeBean = (NodeBean) result.get("currentNodeBean");
        TaskContext taskContext = new TaskContext.Builder(nodeBean.getId(), nodeBean.getName(), jobContext, nodeBean.getType()).build();
        jobContext.putTaskContext(taskContext);
        logger.info("------------ -V- -V- -V- 作业：【{}--{}】 执行开始 -V- -V- -V- ------------", jobId, jobContext.getJobName());
        execTask(taskContext, resultBean);
    }

    */
/**
     * 获取待执行的任务
     *//*

    public static void getNextTask(TaskContext taskContext) {
        ResultBean<List<NodeBean>> resultBean = WorkFlowEngine.getInstance()
                .getNextTask(taskContext.getJobContext().getJobId(), taskContext.getJobContext().getProcessId(), taskContext.getNodeId());
        execTask(taskContext, resultBean);
    }

    */
/**
     * 执行任务
     *
     * @param lastTaskContext 上一任务环境变量
     * @param resultBean      结果
     *//*

    public static void execTask(TaskContext lastTaskContext, ResultBean<List<NodeBean>> resultBean) {
        JobContext jobContext = lastTaskContext.getJobContext();
        if (resultBean.getStatus() == WFConstants.STATUS_CODE_5) {
            processMonitorMap.remove(String.valueOf(jobContext.getProcessId()));
            logger.info("------------ -V- -V- -V- 作业：【{}-{}】, 完成 -V- -V- -V- ------------", jobContext.getJobId(), jobContext.getJobName());
            return;
        }

        Map<String, Object> result = resultBean.getResult();
        if (CollectionUtils.isEmpty(result) || CollectionUtils.isEmpty(resultBean.getT())) {
            logger.info("【{}-{}】，无下一待执行任务", lastTaskContext.getId(), lastTaskContext.getNodeName());
            return;
        }
        List<NodeBean> nodeList = resultBean.getT();
        for (NodeBean nodeBean : nodeList) {
            new Thread(() -> {
                TaskContext currentTaskContext = new TaskContext.Builder(nodeBean.getId(), nodeBean.getName(), jobContext, nodeBean.getType()).build();
                logger.info("【{}-{}】，开始执行", currentTaskContext.getId(), currentTaskContext.getNodeName());
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                jobContext.putTaskContext(currentTaskContext);
                nodeBean.getFromNodeList().forEach(x -> currentTaskContext.getLastTaskContextList().add(jobContext.findTaskContext(x)));
                try {
                    ExecutorFactory.createExecutor(nodeBean.getType(), currentTaskContext).execute();
                } catch (Exception e) {
                    failTaskMonitorMap.put(currentTaskContext.getId(), currentTaskContext);
                    stopWatch.stop();
                    logger.error("【{}-{}】 任务耗时：{}, 执行失败。{}", currentTaskContext.getId(), currentTaskContext.getNodeName(), stopWatch.getTotalTimeSeconds(), e);
                    return;
                }
                stopWatch.stop();
                logger.info("【{}-{}】，结束，任务耗时：{}", currentTaskContext.getId(), currentTaskContext.getNodeName(), stopWatch.getTotalTimeSeconds());
                if (!jobContext.isStop()) {
                    try {
                        WorkFlowCallService2.getNextTask(currentTaskContext);
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

    public static void execFailTask(TaskContext taskContext) {
        logger.info("------------ -V- -V- -V- 任务【{}-{}】，开始重跑 -V- -V- -V- ------------", taskContext.getId(), taskContext.getNodeName());
        try {
            ExecutorFactory.createExecutor(taskContext.getType(), taskContext).execute();
        } catch (Exception e) {
            logger.info("【{}-{}】，重跑失败。", taskContext.getId(), taskContext.getNodeName());
        }
        logger.info("------------ -V- -V- -V- 任务【{}-{}】，重跑成功 -V- -V- -V- ------------", taskContext.getId(), taskContext.getNodeName());
        failTaskMonitorMap.remove(taskContext.getId());
        WorkFlowCallService2.getNextTask(taskContext);
    }

    public static void stopProcess(JobContext jobContext) {
        jobContext.setStop(true);
        logger.info("------------ -V- -V- -V- 作业：【{}-{}】, 停止, 执行中任务仍继续执行 -V- -V- -V- ------------", jobContext.getJobId(), jobContext.getJobName());
    }
*/

}

package com.mingyi.dataroute.executor.http;

import com.mingyi.dataroute.context.JobContext;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.task.http.po.HttpPO;
import com.mingyi.dataroute.persistence.task.http.service.HttpService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.fw4j.core.thread.SignalLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class HttpExecutor implements Executor {

    public static final ConcurrentHashMap<String, SignalLock> lockMap = new ConcurrentHashMap<>();

    private static Logger logger = LoggerFactory.getLogger(HttpExecutor.class);

    private final TaskContext taskContext;
    private final JobContext jobContext;
    private final SignalLock signalLock = new SignalLock(true);

    public HttpExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
        this.jobContext = taskContext.getJobContext();
    }

    @Override
    public void execute() throws Exception {
        // 01-查询任务实体
        HttpPO httpPO = SpringHelp.getBean(HttpService.class).findById(jobContext.getProcessId(), taskContext.getNodeId());

        // 02-处理环境参数
        httpPO.setParams(ParamParser.parseParam(httpPO.getParams(), new ParamTokenHandler(taskContext)));

        // 03-执行请求
        new HttpRunner(taskContext, httpPO).run();
        logger.info("【{}--{}】，请求完成, 加锁等待算法回调", taskContext.getId(), taskContext.getNodeName());
        HttpExecutor.lockMap.put(jobContext.getJobId().toString() + taskContext.getNodeId().toString(), signalLock);
        signalLock.lock();
        try {
            signalLock.await();
        } finally {
            signalLock.unlock();
        }
        HttpExecutor.lockMap.remove(jobContext.getJobId().toString() + taskContext.getNodeId().toString());
        logger.info("【{}--{}】，算法回调成功", taskContext.getId(), taskContext.getNodeName());
    }

}

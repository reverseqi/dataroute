package com.mingyi.dataroute.executor.http;

import com.mingyi.dataroute.executor.ContextParamParser;
import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.persistence.node.http.po.HttpPO;
import com.mingyi.dataroute.persistence.node.http.service.HttpService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.fw4j.core.thread.SignalLock;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * http服务执行器
 * @author vbrug
 * @since 1.0.0
 */
public class HttpExecutor implements Executor {

    public static final  ConcurrentHashMap<Long, SignalLock> asyncLockMap = new ConcurrentHashMap<>();
    private static final Logger                              logger       = LoggerFactory.getLogger(HttpExecutor.class);

    private final TaskContext taskContext;

    public HttpExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        TaskResult taskResult = TaskResult.newInstance(taskContext.getTaskId());
        // 执行请求
        HttpRunner httpRunner = this.buildRunner();
        httpRunner.execute();
        // 设置结果变量
       
        return taskResult.setPrecondition(TaskResult.PRECONDITION_YES);
    }

    /**
     * 构建执行器
     * @return 结果
     */
    private HttpRunner buildRunner() {
        // 查询任务实体
        HttpPO httpPO = SpringHelp.getBean(HttpService.class).findById(taskContext.getNodeId());

        // 处理环境参数
        httpPO.setParams(ContextParamParser.parseParam(httpPO.getParams(), taskContext));

        // 执行请求
        return new HttpRunner(taskContext, new HttpConfigure(httpPO));
    }

}

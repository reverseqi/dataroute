package com.mingyi.dataroute.executor.bsql;

import com.mingyi.dataroute.executor.Executor;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.executor.SQLParser;
import com.mingyi.dataroute.persistence.node.bsql.entity.BSqlPO;
import com.mingyi.dataroute.persistence.node.bsql.service.BSqlService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.context.TaskContext;
import com.vbrug.workflow.core.entity.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 批处理SQL执行器
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(BSqlExecutor.class);


    private final TaskContext taskContext;

    public BSqlExecutor(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public TaskResult execute() throws Exception {
        TaskResult taskResult = TaskResult.newInstance(taskContext.getTaskId());
        // 01-查询任务实体
        BSqlPO bSqlPO = SpringHelp.getBean(BSqlService.class).findById(taskContext.getNodeId());

        // 02-环境变量解析
        bSqlPO.setBsqlPath(ParamParser.parseParam(bSqlPO.getBsqlPath(), new ParamTokenHandler(taskContext)));

        // 03-解析xml
        logger.info("【{}--{}】, 解析SQL文本：{}", taskContext.getTaskId(), taskContext.getTaskName(), bSqlPO.getBsqlPath());
        List<BSqlBean> bSqlBeans = SQLParser.parseBSQL(bSqlPO.getBsqlPath());

        // 04-执行Sql脚本
        logger.info("【{}--{}】, 开始执行sql脚本", taskContext.getTaskId(), taskContext.getTaskName());
        new BSqlRunner(taskContext, bSqlPO).run(bSqlBeans);

        return taskResult.setPrecondition(TaskResult.PRECONDITION_YES).setRemark("");
    }
}

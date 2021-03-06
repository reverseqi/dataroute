package com.mingyi.dataroute.executor;

import com.vbrug.workflow.core.entity.TaskResult;

/**
 * 任务执行接口
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface Executor {

    /**
     * 执行任务
     *
     * @throws Exception 异常
     */
    TaskResult execute() throws Exception;

}

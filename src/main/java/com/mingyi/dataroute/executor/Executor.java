package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.context.JobContext;

/**
 * 任务执行接口
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface Executor {

    void execute() throws Exception;

}

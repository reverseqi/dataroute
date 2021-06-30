package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.executor.bsql.BSqlExecutor;
import com.mingyi.dataroute.executor.export.ExportExecutor;
import com.mingyi.dataroute.executor.extract.ExtractExecutor;
import com.mingyi.dataroute.executor.fileud.FileUDExecutor;
import com.mingyi.dataroute.executor.http.HttpExecutor;
import com.mingyi.dataroute.executor.vimport.ImportExecutor;
import com.vbrug.workflow.core.context.TaskContext;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExecutorFactory {

    public static Executor createExecutor(TaskContext taskContext) {

        switch (TaskType.getByValue(taskContext.getNodeType())) {
            case EXTRACT:
                return new ExtractExecutor(taskContext);
            case BSQL:
                return new BSqlExecutor(taskContext);
            case EXPORT:
                return new ExportExecutor(taskContext);
            case FILE_UD:
                return new FileUDExecutor(taskContext);
            case HTTP:
                return new HttpExecutor(taskContext);
            case IMPORT:
                return new ImportExecutor(taskContext);
            default:
                return null;
        }

    }

}

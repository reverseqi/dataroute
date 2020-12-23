package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.bsql.BSqlExecutor;
import com.mingyi.dataroute.executor.export.ExportExecutor;
import com.mingyi.dataroute.executor.extract.ExtractExecutor;
import com.mingyi.dataroute.executor.fileud.FileUDExecutor;
import com.mingyi.dataroute.executor.http.HttpExecutor;
import com.mingyi.dataroute.executor.vimport.ImportExecutor;

import static com.mingyi.dataroute.constant.WFConstants.*;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExecutorFactory {

    public static Executor createExecutor(String executorType, TaskContext taskContext){

        switch (executorType){
            case TASK_EXTRACT :
                return new ExtractExecutor(taskContext);
            case TASK_BSQL :
                return new BSqlExecutor(taskContext);
            case TASK_EXPORT :
                return new ExportExecutor(taskContext);
            case TASK_FILE_UD :
                return new FileUDExecutor(taskContext);
            case TASK_HTTP :
                return new HttpExecutor(taskContext);
            case TASK_IMPORT :
                return new ImportExecutor(taskContext);
            default :
                return null;
        }

    }

}

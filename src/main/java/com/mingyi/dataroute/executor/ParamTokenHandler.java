package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.parsing.TokenHandler;
import com.vbrug.workflow.core.context.TaskContext;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ParamTokenHandler implements TokenHandler {

    private final TaskContext taskContext;

    public ParamTokenHandler(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    public String handleToken(String content) {
        String value = taskContext.getData().get(content.replaceAll("(\\$|#|\\{|\\})", ""), String.class);
        return content.contains("#") ? "'" + value + "'" : value;
    }
}

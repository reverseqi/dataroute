package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.parsing.TokenHandler;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ParamTokenHandler implements TokenHandler {

    private final TaskContext taskContext;

    public ParamTokenHandler(TaskContext taskContext){
        this.taskContext = taskContext;
    }

    @Override
    public String handleToken(String content) {
        String value = taskContext.getContextDataString(content.replaceAll("(\\$|#|\\{|\\})", ""));
        return content.contains("#") ? "'" +value+"'" : value;
    }
}

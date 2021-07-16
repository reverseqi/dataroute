package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.parsing.BaseTokenParser;
import com.mingyi.dataroute.parsing.TokenHandler;
import com.vbrug.workflow.core.context.TaskContext;

/**
 * 环境变量解析
 * @author vbrug
 * @since 1.0.0
 */
public class ContextParamParser {

    /**
     * 解析文本，替换环境变量
     * @param taskContext 任务环境变量
     * @param text        待处理文本
     * @return 结果
     */
    public static String parseParam(String text, TaskContext taskContext) {
        TokenHandler tokenHandler = content -> {
            String value = (String) taskContext.getContextParam(content.replaceAll("(\\$|#|\\{|\\})", ""));
            return content.contains("#") ? "'" + value + "'" : value;
        };
        return new BaseTokenParser("#{", "}", tokenHandler)
                .parse(new BaseTokenParser("${", "}", tokenHandler).parse(text));
    }
}

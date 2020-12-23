package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.parsing.BaseTokenParser;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ParamParser {

    public static String parseParam(String text, ParamTokenHandler handler) {
        return new BaseTokenParser("#{", "}", handler).parse(new BaseTokenParser("${", "}", handler).parse(text));
    }
}

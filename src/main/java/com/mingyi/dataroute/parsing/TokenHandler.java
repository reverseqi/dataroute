package com.mingyi.dataroute.parsing;

/**
 * 符号解析结果
 *
 * @author vbrug
 * @since 1.0.0
 */
public interface TokenHandler {

    /**
     * 符号解析
     *
     * @param content 符号
     * @return 符号解析结果
     */
    String handleToken(String content);

}

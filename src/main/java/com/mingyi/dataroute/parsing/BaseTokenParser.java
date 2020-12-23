package com.mingyi.dataroute.parsing;


import com.vbrug.fw4j.common.util.StringUtils;

/**
 * Token解析
 *
 * @author vbrug
 * @since v1.0.0
 */
public class BaseTokenParser {


    private final String openToken;
    private final String closeToken;
    private final TokenHandler handler;

    public BaseTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    /**
     * 解析文本
     *
     * @param text 源数据
     * @return 解析结果
     */
    public String parse(String text) {
        // judge text is null
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        int start = text.indexOf(openToken);
        // judge text contain openToken
        if (start == -1) return text;

        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = new StringBuilder();
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                // this open token is escaped. remove the backslash and continue.
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // found open token. let's search close token.
                expression.setLength(0);
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    if (end > offset && src[end - 1] == '\\') {
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    builder.append(handler.handleToken(expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();

    }

}


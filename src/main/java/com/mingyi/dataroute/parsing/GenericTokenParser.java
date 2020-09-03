package com.mingyi.dataroute.parsing;

import org.xml.sax.SAXException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenericTokenParser {

    public static void main(String[] args) throws XPathExpressionException, SAXException, IOException {
        XNode xNode = XPathParser
                .evaluate("/home/vbrug/bsql.xml", "/bsql");

        // xNode.getChildren().forEach(System.out::println);

        List<TokenBean> tokenBeanList = parseToken(xNode.getBody());

        for (TokenBean tokenBean : tokenBeanList) {
            System.out.println(tokenBean.getPlaceholder() + "---" + tokenBean.getKey());

        }

        List<XNode> xNodeList = XPathParser.evaluateList("/home/vbrug/bsql.xml", "/bsql");
        System.out.println(xNodeList.size());

    }

    public static List<TokenBean> parseToken(String input) {

        StringBuilder   sb            = new StringBuilder();
        List<TokenBean> tokenBeanList = new ArrayList<>();

        boolean isQuote = false, isBrace = false, isOpenToken = false;
        String  token   = "";
        for (char c : input.toCharArray()) {
            if(c == '\'') {
                isQuote = isQuote ? false : true;
            } else if ((c == '#' || c == '$') && !isQuote) {
                isOpenToken = true;
                token       = String.valueOf(c);
            } else if (c == '{' && isOpenToken) {
                isBrace = true;
                token += String.valueOf(c);
            } else if (c == '}' && isBrace) {
                token += c;
                TokenBean tokenBean = new TokenBean();
                tokenBean.setPlaceholder(token);
                tokenBean.setKey(tokenBean.getPlaceholder().trim()
                                         .replace("${", "")
                                         .replace("#{", "").replace("}", ""));
                tokenBeanList.add(tokenBean);
                isBrace = false;
                isOpenToken = false;
            } else if (isBrace) {
                token += String.valueOf(c);
            }
        }

        return tokenBeanList;
    }

}


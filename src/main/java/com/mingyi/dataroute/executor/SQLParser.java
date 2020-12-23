package com.mingyi.dataroute.executor;

import com.mingyi.dataroute.executor.bsql.BSqlBean;
import com.mingyi.dataroute.parsing.BaseTokenParser;
import com.mingyi.dataroute.parsing.XNode;
import com.mingyi.dataroute.parsing.XPathParser;
import com.vbrug.fw4j.common.text.TextParser;
import com.vbrug.fw4j.common.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 词解析
 *
 * @author vbrug
 * @since 1.0.0
 */
public class SQLParser {


    /**
     * 解析为word
     *
     * @param sql
     * @return
     */
    public static List<String> parseWord(String sql) {
        sql = sql.replaceAll("\\s", " ");
        return Arrays.asList(sql.split(" "));
    }

    public static String[] clearField(String sqlField){
        int startBraceNumber = 0;
        List<String> chars = new TextParser(sqlField).getWordList();
        List<String> fieldList = new ArrayList<>();
        for (String word : chars) {
            if (word.equals("("))
                startBraceNumber ++;
            else if (word.equals(")"))
                startBraceNumber --;
            else if (startBraceNumber == 0)
                fieldList.add(word);
        }
        return fieldList.toArray(new String[fieldList.size()]);
    }

    public static String getFirstTableName(String sql) {
        List<String> list = SQLParser.parseWord(sql);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase("from")
                    || list.get(i).equalsIgnoreCase("table")) {
                if ((i + 1) < list.size())
                    return list.get(i + 1);
            }
        }
        return null;
    }

    public static String parseToken(String sql, ParamTokenHandler handler) {
        return new BaseTokenParser("#{", "}", handler).parse(new BaseTokenParser("${", "}", handler).parse(sql));
    }

    public static List<BSqlBean> parseBSQL(String uri){
        return SQLParser.parseBSQL(uri, "/bsql");
    }


    /**
     * 解析SQL
     *
     * @param uri
     * @param expression
     * @return
     */
    public static List<BSqlBean> parseBSQL(String uri, String expression) {
        XNode rootNode = XPathParser.evaluate(uri, expression);
        return rootNode.getChildren().stream().map(x -> {
            BSqlBean bSqlBean = new BSqlBean();
            String databaseId = x.getAttributes().getProperty("databaseId");
            if (!StringUtils.isEmpty(databaseId))
                bSqlBean.setDatabaseId(Integer.parseInt(databaseId));

            String id = x.getAttributes().getProperty("id");
            if (StringUtils.hasText(id))
                bSqlBean.setId(Integer.parseInt(id));
            String description = x.getAttributes().getProperty("description");
            if (StringUtils.hasText(description.trim()))
                bSqlBean.setDescription(description.trim());
            bSqlBean.setSql(x.getBody());
            bSqlBean.setStatementType(BSqlBean.StatementType.getByValue(x.getName()));
            return bSqlBean;
        }).collect(Collectors.toList());
    }
}

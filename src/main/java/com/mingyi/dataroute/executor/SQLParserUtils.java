package com.mingyi.dataroute.executor;

import com.vbrug.fw4j.common.util.third.text.TextParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SQL解析工具类
 *
 * @author vbrug
 * @since 1.0.0
 */
public class SQLParserUtils {

    public static final String SQL_KEY_FROM  = "FROM";
    public static final String SQL_KEY_TABLE = "TABLE";

    /**
     * 获取SQL中的
     * @param sql SQL文本
     * @return word集合
     */
    public static List<String> parseWord(String sql) {
        sql = sql.replaceAll("\\s", " ").replaceAll("[ ]+", " ");
        return Arrays.asList(sql.split(" "));
    }

    /**
     * 获取删除表名
     * @param deleteSql 删除SQL
     * @return 表名
     */
    public static String getTableName(String deleteSql) {
        List<String> list = SQLParserUtils.parseWord(deleteSql);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalsIgnoreCase(SQL_KEY_FROM)
                    || list.get(i).equalsIgnoreCase(SQL_KEY_TABLE)) {
                if ((i + 1) < list.size())
                    return list.get(i + 1);
            }
        }
        return null;
    }

    public static String[] clearField(String sqlField) {
        int          startBraceNumber = 0;
        List<String> chars            = new TextParser(sqlField).getWordList();
        List<String> fieldList        = new ArrayList<>();
        for (String word : chars) {
            if (word.equals("("))
                startBraceNumber++;
            else if (word.equals(")"))
                startBraceNumber--;
            else if (startBraceNumber == 0)
                fieldList.add(word);
        }
        return fieldList.toArray(new String[fieldList.size()]);
    }


}

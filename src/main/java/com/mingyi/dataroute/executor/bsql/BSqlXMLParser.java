package com.mingyi.dataroute.executor.bsql;

import com.mingyi.dataroute.db.StatementType;
import com.mingyi.dataroute.parsing.XMLNode;
import com.mingyi.dataroute.parsing.XMLParser;
import com.vbrug.fw4j.common.util.EncryptUtils;
import com.vbrug.fw4j.common.util.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BSql XML文件解析工具类
 * @author vbrug
 * @since 1.0.0
 */
public class BSqlXMLParser {

    private static final String URI_BSQL = "/bsql";

    // xml文件属性
    private static final String PROPERTY_DATABASE_ID = "databaseId";
    private static final String PROPERTY_ID          = "id";
    private static final String PROPERTY_DESCRIPTION = "description";

    /**
     * 解析bsql
     * @param uri 文件URI路径
     * @return 结果
     */
    public static List<BSqlBean> parseBSQL(String uri) throws Exception {
        return parseBSQL(EncryptUtils.parseFile2(uri), URI_BSQL);
    }


    /**
     * 解析bsql
     * @param is         文件流
     * @param expression 解析路径
     * @return 结果
     */
    public static List<BSqlBean> parseBSQL(InputStream is, String expression) {
        return parseBSQL(XMLParser.evaluate(is, expression));
    }

    /**
     * 解析BSQL
     * @param uri        文件URI路径
     * @param expression 解析路径
     * @return 结果
     */
    public static List<BSqlBean> parseBSQL(String uri, String expression) {
        return parseBSQL(XMLParser.evaluate(uri, expression));
    }

    /**
     * 解析BSQL
     * @param rootNode   XNode文档节点
     * @param expression 解析路径
     * @return 结果
     */
    private static List<BSqlBean> parseBSQL(XMLNode rootNode) {
        return rootNode.getChildren().stream().map(x -> {
            BSqlBean bSqlBean   = new BSqlBean();
            String   databaseId = x.getAttributes().getProperty(PROPERTY_DATABASE_ID);
            if (!StringUtils.isEmpty(databaseId))
                bSqlBean.setDatabaseId(Integer.parseInt(databaseId));

            String id = x.getAttributes().getProperty(PROPERTY_ID);
            if (StringUtils.hasText(id))
                bSqlBean.setId(Integer.parseInt(id));

            String description = x.getAttributes().getProperty(PROPERTY_DESCRIPTION);
            if (StringUtils.hasText(description.trim()))
                bSqlBean.setDescription(description.trim());

            bSqlBean.setSql(x.getBody());
            bSqlBean.setStatementType(StatementType.getByValue(x.getName()));
            return bSqlBean;
        }).collect(Collectors.toList());
    }
}

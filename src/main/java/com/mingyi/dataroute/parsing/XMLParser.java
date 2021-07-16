package com.mingyi.dataroute.parsing;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML文件解析工具
 */
public class XMLParser {

    private static DocumentBuilder dBuilder = null;
    private static XPath           xPath    = null;

    static {
//        dBuilderFactory.setValidating(true);
//        dBuilderFactory.setCoalescing(true);
        DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
        dBuilderFactory.setIgnoringElementContentWhitespace(true);              // 忽略内容中的空格
        dBuilderFactory.setIgnoringComments(true);                              // 忽略注释
        try {
            dBuilder = dBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        xPath = XPathFactory.newInstance().newXPath();
        dBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
    }

    /**
     * 解析XML文件
     * @param uri        XML文件URI路径
     * @param expression 解析表达式
     * @return 结果
     */
    public static XMLNode evaluate(String uri, String expression) {
        try {
            return evaluate(dBuilder.parse(uri), expression);
        } catch (Exception e) {
            throw new XMLParserException(e);
        }
    }

    /**
     * 解析XML文件
     * @param is         XML文件流
     * @param expression 解析表达式
     * @return 结果
     */
    public static XMLNode evaluate(InputStream is, String expression) {
        try {
            return evaluate(dBuilder.parse(is), expression);
        } catch (Exception e) {
            throw new XMLParserException(e);
        }
    }

    /**
     * 获取List集合
     * @param uri        文件URI地址
     * @param expression xml路径表达式
     * @return 结果集合
     */
    public static List<XMLNode> evaluateList(String uri, String expression) {
        NodeList nodeList = null;
        Document document = null;
        try {
            document = dBuilder.parse(uri);
            nodeList = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new XMLParserException(e);
        }
        // 判断是否有数据
        if (nodeList == null || nodeList.getLength() == 0)
            return null;

        // 将W3C节点转换为XML节点
        List<XMLNode> XMLNodeList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            XMLNode XMLNode = new XMLNode();
            XMLNodeList.add(XMLNode);
            recursiveParse(XMLNode, nodeList.item(i));
        }
        return XMLNodeList;
    }

    /**
     * 解析XML文档
     * @param document   xml文档
     * @param expression xml路径表达式
     * @return 解析结果
     */
    private static XMLNode evaluate(Document document, String expression) throws XPathExpressionException {
        Node    parseNode = (Node) xPath.evaluate(expression, document, XPathConstants.NODE);
        XMLNode XMLNode   = new XMLNode();
        recursiveParse(XMLNode, parseNode);
        return XMLNode;
    }

    /**
     * 遍历解析
     * @param XMLNode XML节点
     * @param node    XPath 节点
     */
    private static void recursiveParse(XMLNode XMLNode, Node node) {
        XMLNode.setName(node.getNodeName());
        XMLNode.setBody(node.getTextContent().trim());
        // 属性解析
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                XMLNode.getAttributes().setProperty(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
            }
        }
        // 子节点解析
        if (node.getChildNodes() != null && node.getChildNodes().getLength() > 0) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node item = node.getChildNodes().item(i);
                if (item.getNodeType() == 1) {
                    XMLNode child = new XMLNode();
                    XMLNode.getChildren().add(child);
                    recursiveParse(child, item);
                }
            }
        }
    }

}

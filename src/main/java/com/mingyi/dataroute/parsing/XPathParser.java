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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XPathParser {

    private static DocumentBuilderFactory dBuilderFactory = null;

    private static DocumentBuilder dBuilder = null;

    private static XPathFactory xPathFactory = null;

    private static XPath xPath = null;


    static {
        dBuilderFactory = DocumentBuilderFactory.newInstance();

//        dBuilderFactory.setValidating(true);

        dBuilderFactory.setIgnoringElementContentWhitespace(true);

//        dBuilderFactory.setCoalescing(true);

        dBuilderFactory.setIgnoringComments(true);

        try {
            dBuilder = dBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        xPathFactory = XPathFactory.newInstance();

        xPath = xPathFactory.newXPath();

        dBuilder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws
                    SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws
                    SAXException {
                throw exception;
            }
        });
    }


    public static XNode evaluate(String uri, String expression) {
        Node parseNode = null;
        try {

            Document document = dBuilder.parse(uri);
            parseNode = (Node) xPath
                    .evaluate(expression, document, XPathConstants.NODE);
        } catch (Exception e) {
            throw new XPathParserException(e);
        }

        XNode xNode = new XNode();

        recursiveParse(xNode, parseNode);

        return xNode;

    }


    public static XNode evaluate(InputStream is, String expression) {
        Node parseNode = null;
        try {

            Document document = dBuilder.parse(is);
            parseNode = (Node) xPath
                    .evaluate(expression, document, XPathConstants.NODE);
        } catch (Exception e) {
            throw new XPathParserException(e);
        }

        XNode xNode = new XNode();

        recursiveParse(xNode, parseNode);

        return xNode;

    }

    public static List<XNode> evaluateList(String uri, String expression) throws
            IOException, SAXException, XPathExpressionException {

        NodeList nodeList = null;
        try {

            Document document = dBuilder.parse(uri);
            nodeList = (NodeList) xPath
                    .evaluate(expression, document, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new XPathParserException(e);
        }

        if (nodeList == null && nodeList.getLength() == 0)
            return null;

        List<XNode> xNodeList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            XNode xNode = new XNode();
            xNodeList.add(xNode);
            recursiveParse(xNode, nodeList.item(i));
        }

        return xNodeList;
    }

    private static void recursiveParse(XNode xNode, Node node) {
        xNode.setName(node.getNodeName());
        xNode.setBody(node.getTextContent().trim());
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                xNode.getAttributes().setProperty(attributes.item(i)
                        .getNodeName(), attributes.item(i).getNodeValue());
            }
        }

        if (node.getChildNodes() != null &&
                node.getChildNodes().getLength() > 0) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node item = node.getChildNodes().item(i);
                if (item.getNodeType() == 1) {
                    XNode child = new XNode();
                    xNode.getChildren().add(child);
                    recursiveParse(child, item);
                }
            }
        }
    }


}

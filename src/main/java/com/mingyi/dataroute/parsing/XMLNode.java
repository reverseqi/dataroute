package com.mingyi.dataroute.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class XMLNode {

    private String        name;
    private String        body;
    private Properties    attributes = new Properties();
    private Properties    variables  = new Properties();
    private List<XMLNode> children   = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public void setAttributes(Properties attributes) {
        this.attributes = attributes;
    }

    public Properties getVariables() {
        return variables;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public List<XMLNode> getChildren() {
        return children;
    }

    public void setChildren(
            List<XMLNode> children) {
        this.children = children;
    }

    public String toString() {
        return this.name + System.lineSeparator() + this.attributes.get("id") +
                System.lineSeparator() + this.body;
    }
}

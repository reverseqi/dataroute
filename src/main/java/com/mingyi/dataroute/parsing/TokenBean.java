package com.mingyi.dataroute.parsing;

public class TokenBean {

    private String key;

    private String placeholder;

    private boolean quote;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isQuote() {
        return this.placeholder.startsWith("#");
    }

    public void setQuote(boolean quote) {
        this.quote = quote;
    }
}

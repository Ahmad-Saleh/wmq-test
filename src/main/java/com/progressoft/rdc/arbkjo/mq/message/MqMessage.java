package com.progressoft.rdc.arbkjo.mq.message;

import java.util.HashMap;
import java.util.Map;

public class MqMessage {

    private String correlationId;
    private String content;

    @Override
    public String toString() {
        return "MqMessage [correlationId=" + correlationId + ", content=" + content + "]";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private Map<String, String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void addHeader(String name, String value) {
        if (headers == null) {
            headers = new HashMap();
        }
        headers.put(name, value);
    }
}
